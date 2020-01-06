package com.rvtechnologies.grigora.view.ui.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.view.ui.MainActivity
import kotlinx.android.synthetic.main.activity_main.*


class AboutUsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.temp_about_us, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {

            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.VISIBLE
            (activity as MainActivity).lockDrawer(true)
        }
    }
}
