package com.app.sharechatdownloader.fragments


import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.sharechatdownloader.MainActivity
import com.app.sharechatdownloader.adapter.VideoAdapter
import com.app.sharechatdownloader.cInterface.CellClickListener
import com.app.sharechatdownloader.databinding.FragmentDownloadBinding
import com.app.sharechatdownloader.helpers.FileUtil
import com.app.sharechatdownloader.helpers.FileUtil.newList
import com.app.sharechatdownloader.model.Video
import java.lang.Exception

class DownloadFragment : Fragment(), CellClickListener {

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private var TAG = "DownloadFragment MESSAGE"

    private val myVideoAdapter by lazy {
        VideoAdapter(
            requireContext(), /*MainActivity.videoList,*/
            this
        )
    }

    // using broadcast method
    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id:Long = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
            //Log.d(TAG,"ExtraDownloadid ${id} and Actual DownloadId ${FileUtil.downloadId}")
            if (FileUtil.downloadId == id) {
                var list = newList(requireContext())
                myVideoAdapter?.setData(list)
                //Scroll to First Position to display recent downloaded videos
                binding.VideoRV.scrollToPosition(0)
               /* binding.VideoRV.scrollToPosition(myVideoAdapter.getItemCount() - 1)*/
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        //register for receiver
        activity?.registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        );
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController() //Initialising navController

        binding.VideoRV.setHasFixedSize(true)
        binding.VideoRV.setItemViewCacheSize(10)

        binding!!.refreshlist.setOnRefreshListener {
            myVideoAdapter.setData(newList(requireContext()))
            binding.refreshlist.isRefreshing = false
        }

        binding!!.apply {
            VideoRV.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            VideoRV.adapter = myVideoAdapter.also {
                it.setData(newList(requireContext()))
            }
        }

    }


    //Click Listener interface
    override fun onCellClickListener(data: Video) {
        //Toast.makeText(context,"Cell clicked at position ${data.id}", Toast.LENGTH_SHORT).show()
        playVideo(data)
    }

    // play video file via implicit intent
    //-------Left To add checking for videoplayer package
    fun playVideo(data: Video) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(data.artUri, "video/*")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // using broadcast method
        context?.unregisterReceiver(onDownloadComplete);
        _binding = null
    }
}
