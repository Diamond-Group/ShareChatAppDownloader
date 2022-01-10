package com.android.atr07.drawerwithbottomnavigation.fragments


import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope

import com.android.atr07.drawerwithbottomnavigation.R
import com.android.atr07.drawerwithbottomnavigation.databinding.FragmentProfileBinding
import helper.FileUtil
import helper.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import com.android.atr07.drawerwithbottomnavigation.MainActivity

import helper.FileUtil.downloadId


/**
 * ProfileFragment
 */

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    //  private lateinit var binding: FragmentProfileBinding

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val TAG = "Custom Message"
    private var message: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)



        binding.downloadBtn.setOnClickListener {
            getShareChatData()
        }

        //register for receiver
        /*getActivity()?.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        );*/


        // Inflate the layout for this fragment
        return binding.root

    }

    private fun getShareChatData() {
        val enteredUrl = binding.sharechatUrl.getText().toString()


        if (enteredUrl != null && enteredUrl.isNotEmpty()) {
            Log.d(TAG, "Entered Url is ${enteredUrl}")
            var url: URL? = null

            try {
                url = URL(enteredUrl)
                var host: String = url.host
                if (host.contains("sharechat")) {

                    //coroutine
                    lifecycleScope.launch(Dispatchers.IO) {
                        //returns either videourl or null from java helper class
                        val urlResult = async { getVideoUrl(enteredUrl) }

                        val videoUrl = urlResult.await()

                        if (videoUrl != null) {

                            //Calling Download Manager in FileUtil to Download Files
                            message = FileUtil.saveFilesToLocalStorage(videoUrl, requireContext())

                            //Displaying Files storage related message

                                lifecycleScope.launch(Dispatchers.Main){
                                    Toast.makeText(context, "${message}", Toast.LENGTH_SHORT).show()
                                }

                        } else {
                            lifecycleScope.launch(Dispatchers.Main){
                            Toast.makeText(context, "Internal Application Error It will be fixed soon", Toast.LENGTH_SHORT
                            ).show() }
                        }
                    }

                } else {
                    Toast.makeText(context, "Enter valid url", Toast.LENGTH_SHORT).show()
                }


            } catch (e: MalformedURLException) {
                Toast.makeText(context, "Enter valid url", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "${e.printStackTrace()}")
            }
        } else {
            Toast.makeText(context, "Enter valid url", Toast.LENGTH_SHORT).show()
        }


    }


    fun getVideoUrl(vararg strings: String?): String? {
        val scDocument: Document
        var videoUrl: String? = null
        try {
            scDocument = Jsoup.connect(strings[0])
                .header("Accept-Language", "en-US")
                .header("Connection", "keep-alive")
                .header(
                    "User-Agent",
                    "Mozilla/5.0"
                )
                .get()
            //sharechatUrlExtractor defined below
            videoUrl = Util.shareChatUrlExtractor(scDocument)

            videoUrl?.let {
                Log.d(TAG, "Video Url is - ${videoUrl}")
            }


        } catch (e: IOException) {
            Log.d(TAG, "${e.printStackTrace()}")
        }
        return videoUrl
    }

    // using broadcast method
    /*private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadId === id) {
                Toast.makeText(getContext(), "Download Completed", Toast.LENGTH_SHORT).show()
            }
        }
    }*/




    /*
    suspend fun saveFilesToLocalStorage(videoUrl: String) {
        GlobalScope.async(Dispatchers.IO) {

            if (videoUrl != "") {

                /*  Util.download(videoUrl, Util.RootDirectoryForShareChat, ShareChatActivity.this,
                       "Sharechat " + System.currentTimeMillis() + ".mp4");*/


                //downloadmanager start
                val fileName = "Sharechat" + System.currentTimeMillis() + ".mp4"

                val uri = Uri.parse(videoUrl)
                val request = DownloadManager.Request(uri)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(true).setTitle("Some name")
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDescription("Downloading file")
                    .setTitle(fileName)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_PICTURES,
                        File.separator + "Sharechat Video" + File.separator + fileName
                    )

                val mgr = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
               downloadId = mgr.enqueue(request)

                Toast.makeText(context, "Downloading successfully", Toast.LENGTH_SHORT)
                    .show()

                //downloadmanager ends
            } else {
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

 */

}
