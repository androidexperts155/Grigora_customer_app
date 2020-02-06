package com.rvtechnologies.grigora.view.ui.restaurant_list.adapter

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_list.RestaurantDetailsFragment
import com.rvtechnologies.grigora.view.ui.restaurant_list.TableBookingFragment

class RestaurantPagerAdapter(
    fragmentManager: FragmentManager,
    var activity: Activity,
    var arguments: Bundle?,val iRecyclerItemClick: IRecyclerItemClick
) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            RestaurantDetailsFragment(arguments?.get(AppConstants.RESTAURANT_ID).toString(),iRecyclerItemClick)
        } else {
            TableBookingFragment(arguments)
        }

    }

    override fun getCount(): Int {
       if( arguments?.get(AppConstants.RESTAURANT_BOOKING).toString().equals("0")){
           return  1
       }else
        return 2
    }


}