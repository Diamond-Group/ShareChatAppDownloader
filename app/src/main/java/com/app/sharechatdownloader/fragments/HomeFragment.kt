package com.app.sharechatdownloader.fragments


import android.app.DownloadManager
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.app.sharechatdownloader.databinding.FragmentHomeBinding
import com.app.sharechatdownloader.helpers.FileUtil
import com.app.sharechatdownloader.helpers.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL


class HomeFragment : Fragment() { //OnClickListener

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "HomeFragment Message"
    private var message: String? = null
    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController() //Initialising navController

        //Initialising button click event listener
        binding.downloadBtn.setOnClickListener {
            getShareChatData()
            binding.sharechatUrl.setText("");
        }

        binding.pasteBtn.setOnClickListener {
            var str = getTextFromClipboard(requireContext())
            str?.let {
                binding.sharechatUrl.setText(str)
            }
        }

    }

    private fun getTextFromClipboard(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        var pasteData = ""

        // If it does contain data, decide if you can handle the data.
        if (!clipboard!!.hasPrimaryClip()) {
        } else if (!clipboard!!.primaryClipDescription!!.hasMimeType(
                ClipDescription.MIMETYPE_TEXT_PLAIN
            )
        ) {

            // since the clipboard has data but it is not plain text
        } else {

            //since the clipboard contains plain text.
            val item = clipboard!!.primaryClip!!.getItemAt(0)

            // Gets the clipboard as text.
            pasteData = item.text.toString()
        }
        return pasteData
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

                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "${message}", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Internal Application Error It will be fixed soon",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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
                    "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}