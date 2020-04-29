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
        if (arguments?.get(AppConstants.PAGE_TYPE) == 5){
            (activity as MainActivity).backTitle(getString(R.string.privacy_policy))
        }
       else if (arguments?.get(AppConstants.PAGE_TYPE) == 2)
            (activity as MainActivity).backTitle(getString(R.string.termsandconditions))
        else
            (activity as MainActivity).backTitle(getString(R.string.about_us))
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var address =
            when {
                arguments?.get(AppConstants.PAGE_TYPE) == 5 -> getString(R.string.privacy_policy_url)
                arguments?.get(AppConstants.PAGE_TYPE) == 2 -> getString(R.string.terms_and_conditions)
                GrigoraApp.getInstance()
                    .getCurrentLanguage() == AppConstants.FRENCH -> getString(R.string.about_us_url_fr)
                else -> getString(R.string.about_us_url_en)
            }


        web_view.getSettings().javaScriptEnabled = true
        web_view.webViewClient = object : WebViewClient() {}
        web_view.loadUrl(address)


    }
}
