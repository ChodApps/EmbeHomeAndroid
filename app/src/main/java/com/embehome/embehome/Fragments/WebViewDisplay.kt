package com.embehome.embehome.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import kotlinx.android.synthetic.main.fragment_web_view_display.view.*

class WebViewDisplay : Fragment() {

    val args : WebViewDisplayArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.title == null || args.url == null) findNavController().navigateUp()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_web_view_display, container, false)
        AppDialogs.startLoadScreen(requireContext())

        if (args.title != null) v.textView166.text = args.title

        v.webView.settings.javaScriptEnabled = true

        v.imageView215.setOnClickListener {
            findNavController().navigateUp()
        }

        v.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                AppDialogs.stopLoadScreen()
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                AppServices.log("TronX Web View",error?.description.toString())
                super.onReceivedError(view, request, error)
            }
        }

        args.url?.let {
            v.webView.loadUrl(it)
        }


        return v
    }
}