package com.rvtechnologies.grigora.view.ui.orders.adapter

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.view.ui.orders.OrderFragment

class OrderPagerAdapter(fragmentManager: FragmentManager,var activity: Activity) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 3
    }
    override fun getItem(position: Int): Fragment {
        return OrderFragment.newInstance(position)
    }
    override fun getPageTitle(position: Int): CharSequence? {
        return if(position==0) activity.getString(R.string.current_orders) else if(position==1)activity.getString(R.string.past_orders) else activity.getString(R.string.upcoming_orders)
    }
}