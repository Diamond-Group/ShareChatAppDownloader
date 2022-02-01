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
import com.app.sharechatdownloader.databinding.FragmentDisclaimerBinding

class DisclaimerFragment : Fragment() {
    private var _binding: FragmentDisclaimerBinding? = null
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
        _binding = FragmentDisclaimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController() //Initialising navController

        var currentUrl: String? = null

        binding.progressBarD.max = 100

        binding.webViewD.loadUrl("https://anmolhiragroup1.blogspot.com/2022/01/interpretation-and-definitions.html")
        binding.webViewD.settings.javaScriptEnabled = true

        binding.webViewD.setWebViewClient(object : WebViewClient() {
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