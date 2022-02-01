package com.app.sharechatdownloader.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.sharechatdownloader.MainActivity
import com.app.sharechatdownloader.R
import com.app.sharechatdownloader.cInterface.CellClickListener
import com.app.sharechatdownloader.databinding.DetailsViewBinding
import com.app.sharechatdownloader.databinding.VideoMoreFeaturesBinding
import com.app.sharechatdownloader.databinding.VideoViewBinding
import com.app.sharechatdownloader.helpers.FileUtil
import com.app.sharechatdownloader.model.Video
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import java.io.File


class VideoAdapter(
    private val context: Context,
    private val cellClickListener: CellClickListener
) :
    RecyclerView.Adapter<VideoAdapter.MyHolder>() {
    private var newPosition = 0
    private var videoList = emptyList<Video>()

    class MyHolder(binding: VideoViewBinding) : RecyclerView.ViewHolder(binding.root) {

        val title = binding.videoName
        val duration = binding.duration
        val image = binding.videoImg
        val root = binding.root
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(VideoViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = videoList[position].title
        //holder.folder.text = videoList[position].folderName
        holder.duration.text = DateUtils.formatElapsedTime(videoList[position].duration / 1000)
        Glide.with(context)
            .asBitmap()
            .load(videoList[position].thumbnailUri)
            .apply(RequestOptions.placeholderOf(R.drawable.ic_video_placeholder).centerCrop())
            .into(holder.image);

        val data = videoList[position]

        //onClickListener
        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(data)
        }

        holder.root.setOnLongClickListener {
            //setting position for delete
            position.also { newPosition = it }

            val customDialog = LayoutInflater.from(context)
                .inflate(R.layout.video_more_features, holder.root, false)
            val bindingMF = VideoMoreFeaturesBinding.bind(customDialog)
            val dialog = MaterialAlertDialogBuilder(context).setView(customDialog)
                .create()
            dialog.show()

            bindingMF.shareBtn.setOnClickListener {
                dialog.dismiss()
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "video/*"
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoList[position].path))
                ContextCompat.startActivity(
                    context, Intent.createChooser(shareIntent, "Sharing Video File"),
                    null
                )
            }

            bindingMF.infoBtn.setOnClickListener {
                dialog.dismiss()
                val customDialogIF =
                    LayoutInflater.from(context).inflate(R.layout.details_view, holder.root, false)
                val bindingIF = DetailsViewBinding.bind(customDialogIF)
                val dialogIF = MaterialAlertDialogBuilder(context).setView(customDialogIF)
                    .setCancelable(false)
                    .setPositiveButton("Ok") { self, _ ->
                        self.dismiss()
                    }
                    .create()
                dialogIF.show()
                val infoText = SpannableStringBuilder().bold { append("DETAILS\n\nName: ") }
                    .append(videoList[position].title)
                    .bold { append("\n\nDuration: ") }
                    .append(DateUtils.formatElapsedTime(videoList[position].duration / 1000))
                    .bold { append("\n\nFile Size: ") }.append(
                        Formatter.formatShortFileSize(
                            context,
                            videoList[position].size.toLong()
                        )
                    )
                    .bold { append("\n\nLocation: ") }.append(videoList[position].path)

                bindingIF.detailTV.text = infoText
                dialogIF.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                    MaterialColors.getColor(context, R.attr.themeColor, Color.LTGRAY)
                )
            }

            bindingMF.deleteBtn.setOnClickListener {
                dialog.dismiss()
                requestDeleteR(position = position)
                setData(FileUtil.newList(context))

            }


            return@setOnLongClickListener true
        }
    }

    //delete video file
    private fun requestDeleteR(position: Int) {
        //list of videos to delete
        val uriList: List<Uri> = listOf(
            Uri.withAppendedPath(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoList[position].id
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            //requesting for delete permission
            val pi = MediaStore.createDeleteRequest(context.contentResolver, uriList)
            (context as Activity).startIntentSenderForResult(
                pi.intentSender, 123,
                null, 0, 0, 0, null
            )
        } else {
            //for devices less than android 11
            val file = File(videoList[position].path)
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Delete Video?")
                .setMessage(videoList[position].title)
                .setPositiveButton("Yes") { self, _ ->
                    if (file.exists() && file.delete()) {
                        MediaScannerConnection.scanFile(context, arrayOf(file.path), null, null)
                        updateDeleteUI(position = position)
                    }
                    self.dismiss()
                }
                .setNegativeButton("No") { self, _ -> self.dismiss() }
            val delDialog = builder.create()
            delDialog.show()
            delDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                MaterialColors.getColor(context, R.attr.themeColor, Color.LTGRAY)
            )
            delDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(
                MaterialColors.getColor(context, R.attr.themeColor, Color.LTGRAY)
            )
        }
    }

    private fun updateDeleteUI(position: Int) {
        // notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    fun setData(newVideoList: List<Video>) {
        val diffUtil = VideoDiffCallback(videoList, newVideoList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        /* this.videoList.clear()
         this.videoList.addAll(newVideoList)*/
        videoList = newVideoList
        diffResults.dispatchUpdatesTo(this)
    }

}
