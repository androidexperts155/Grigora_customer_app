package com.rvtechnologies.grigora.view.ui.profile


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.view.ui.MainActivity
import kotlinx.android.synthetic.main.about_us_fragment.*
import kotlinx.android.synthetic.main.activity_main.*


class AboutUsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.about_us_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.about_us))
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        web_view.getSettings().javaScriptEnabled = true
        web_view.webViewClient = object : WebViewClient() {}

        var address= if (GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH) "http://3.13.78.53/about_us/fr/" else "http://3.13.78.53/about_us/en/"
        web_view .loadUrl(address)


    }
}
