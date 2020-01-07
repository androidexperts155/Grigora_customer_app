package com.rvtechnologies.grigora.view.ui.orders


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.orders.adapter.OrderPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_orders.*


class OrdersParentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vpOrders.adapter =
            childFragmentManager.let { activity?.let { it1 -> OrderPagerAdapter(it, it1) } }


        tv_current.setOnClickListener {
            tv_current.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_past.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

            tv_current.setBackgroundResource(
                R.drawable.left_round_dark
            )
            tv_past.setBackgroundResource(
                R.drawable.right_round_light
            )

            vpOrders.currentItem = 0
        }

        tv_past.setOnClickListener {
            tv_current.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            tv_past.setTextColor(ContextCompat.getColor(context!!, R.color.white))

            tv_current.setBackgroundResource(
                R.drawable.left_round_light
            )
            tv_past.setBackgroundResource(
                R.drawable.right_round_dark
            )
            vpOrders.currentItem = 1
        }


        vpOrders.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0)
                    tv_current.callOnClick()
                else
                    tv_past.callOnClick()
            }

        })
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


    companion object {
        @JvmStatic
        fun newInstance() =
            OrdersParentFragment()
    }
}