package com.rvtechnologies.grigora.view.ui.login_signup


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.login_signup.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_welcome_info.*

class WelcomeInfoFragment : Fragment() {
    var current = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CommonUtils.changeStatusBarColor(
            ContextCompat.getColor(context!!, R.color.lightGrey),
            activity as MainActivity
        )

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CommonUtils.saveBooleanPrefs(context!!, PrefConstants.IS_NOTIFICATIONS_ON, true)
        vp.adapter = ViewPagerAdapter(context!!)

        dots_indicator.setViewPager(vp);

        tv_next.setOnClickListener {
            if (tv_next.text.toString().equals("Finish")) {
                save()
                view?.findNavController()
                    ?.navigate(R.id.action_welcomeFragment_to_SelectLanguage)

            } else {
                current++
                vp.currentItem = current
            }
        }
        tv_skip.setOnClickListener {
            save()
            view?.findNavController()
                ?.navigate(R.id.action_welcomeFragment_to_SelectLanguage)
        }

        vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                current = position
                if (position == 4)
                    tv_next.text = "Finish"
                else
                    tv_next.text = "Next"

            }
        })

    }

    fun save() {
        CommonUtils.savePermanentBooleanPrefs(context, PrefConstants.IS_NOT_FIRST, true)
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).lockDrawer(true)
        }
    }
}
