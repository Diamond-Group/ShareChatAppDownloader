package com.app.sharechatdownloader.others

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.app.sharechatdownloader.databinding.FragmentPrivacypolicyBinding


class PrivacyPolicy : Fragment() {

    private var _binding: FragmentPrivacypolicyBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    companion object {
        const val MAX_PROGRESS = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPrivacypolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController() //Initialising navController

        var currentUrl: String? = null

        binding.progressBar.max = 100

        binding.webView.loadUrl("https://anmolhiragroup.blogspot.com/2022/01/privacy-policy.html")
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.setWebViewClient(object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                currentUrl = url
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}