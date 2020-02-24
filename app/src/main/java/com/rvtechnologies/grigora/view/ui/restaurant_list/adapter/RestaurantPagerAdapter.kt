package com.rvtechnologies.grigora.view.ui.restaurant_list.adapter

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_list.GroupOrderFragment
import com.rvtechnologies.grigora.view.ui.restaurant_list.RestaurantDetailsFragment
import com.rvtechnologies.grigora.view.ui.restaurant_list.TableBookingFragment

class RestaurantPagerAdapter(
    fragmentManager: FragmentManager,
    var activity: Activity,
    var arguments: Bundle?, val iRecyclerItemClick: IRecyclerItemClick
) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {

        if (count == 3) {
            return if (position == 0) {
                RestaurantDetailsFragment(
                    arguments?.get(AppConstants.RESTAURANT_ID).toString(),
                    iRecyclerItemClick
                )
            } else if (position == 1) {
                TableBookingFragment(arguments)
            } else {
                GroupOrderFragment(
                    arguments, iRecyclerItemClick
                )
            }
        } else {
            return if (position == 0) {
                RestaurantDetailsFragment(
                    arguments?.get(AppConstants.RESTAURANT_ID).toString(),
                    iRecyclerItemClick
                )
            } else {
                GroupOrderFragment(
                    arguments, iRecyclerItemClick
                )
            }
        }

        return if (position == 0) {
            RestaurantDetailsFragment(
                 arguments?.get(AppConstants.RESTAURANT_ID).toString(),
                 iRecyclerItemClick
            )
        } else {
            TableBookingFragment(arguments)
        }

    }

    override fun getCount(): Int {
        return if (arguments?.get(AppConstants.RESTAURANT_BOOKING).toString() == "0") {
            2
        } else
            3
    }
}