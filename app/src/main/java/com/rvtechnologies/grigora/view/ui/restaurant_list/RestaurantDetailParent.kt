package com.rvtechnologies.grigora.view.ui.restaurant_list


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantPagerAdapter
import kotlinx.android.synthetic.main.fragment_restaurant_detail_parent.*

class RestaurantDetailParent : Fragment(), IRecyclerItemClick {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_restaurant_detail_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppConstants.CURRENT_SELECTED = 0
        if ((arguments?.containsKey(AppConstants.CART_ID)!!)) {
            tab_top.visibility = View.GONE
        }
        if ((arguments?.containsKey(AppConstants.IS_FOR_GROUP_ORDER)!!)) {
            tab_top.visibility = View.GONE
        } else if ((arguments?.containsKey(AppConstants.FROM_PICKUP)!!) && arguments?.get(
                AppConstants.FROM_PICKUP
            ) as Boolean
        ) {
            tab_top.visibility = View.GONE
        } else {
            if (arguments?.get(AppConstants.RESTAURANT_BOOKING).toString() == "0") {
                tab_top.removeTabAt(1)
            }
        }

        img_back.setOnClickListener {
            view?.findNavController()?.popBackStack()
        }

        for (i in 0 until tab_top.tabCount) {

            //noinspection ConstantConditions
            var tv = layoutInflater.inflate(R.layout.tab_textview, null) as TextView
            tv.text = tab_top.getTabAt(i)?.text

            if (tab_top.getTabAt(i)?.isSelected!!) {
                tv.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            } else {
                if (CommonUtils.isDarkMode())
                    tv.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                else
                    tv.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            }
            tab_top.getTabAt(i)?.customView = tv

        }
        tab_top.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                var view: View? = p0!!.customView

                view as TextView

                if (CommonUtils.isDarkMode())
                    view.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                else
                    view.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                var view: View? = p0!!.customView
                view as TextView
                view.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

                when {
                    p0!!.text.toString().equals(getString(R.string.order)) -> {
                        vp_fragments.currentItem = 0
                    }
                    p0!!.text.toString().equals(getString(R.string.book_a_table)) -> {
                        if (CommonUtils.isLogin())
                            vp_fragments.currentItem = 1
                        else {
                            (activity as MainActivity).showLoginAlert()
                            tab_top.getTabAt(0)?.select()
                        }
                    }
                    p0!!.text.toString().equals(getString(R.string.group_order)) -> {
                        if (CommonUtils.isLogin())
                            vp_fragments.currentItem = 2
                        else {
                            tab_top.getTabAt(0)?.select()
                            (activity as MainActivity).showLoginAlert()
                        }
                    }
                }

                AppConstants.CURRENT_SELECTED = p0.position
            }
        })


        var cartId = if (arguments?.containsKey(AppConstants.CART_ID)!!) {
            arguments?.getString(AppConstants.CART_ID)!!

        } else
            ""
        vp_fragments.adapter = RestaurantPagerAdapter(
            childFragmentManager,
            (activity as MainActivity),
            arguments, this
        )
        vp_fragments.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {


            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    (activity as MainActivity).updateCartButton()
                } else {
                    (activity as MainActivity).hideAll()
                }
            }

        })
    }

    override fun onItemClick(item: Any) {
        if (item is Bundle) {
            view?.findNavController()
                ?.navigate(
                    R.id.action_restaurantDetailParent_to_menuItemDetailsFragment,
                    item
                )
        } else if (item is String) {
            var arg = arguments
            arg?.putBoolean(AppConstants.IS_FOR_GROUP_ORDER, true)
            arg?.putString(AppConstants.CART_ID, item)

            view?.findNavController()
                ?.navigate(
                    R.id.action_restaurantDetailParent_to_restaurantDetailGroup, arg
                )
        } else if (item is Int) {
            when (item) {
                1 -> {
                    tab_top.visibility = View.GONE
                }
                2 -> {
                    var bundle = bundleOf(
                        AppConstants.RESTAURANT_ID to arguments?.get(AppConstants.RESTAURANT_ID)!!
                            .toString()
                    )

                    view?.findNavController()
                        ?.navigate(
                            R.id.action_restaurantDetailParent_to_reviewsFragment, bundle
                        )
                }
                3 -> {
                    var bundle = bundleOf(
                        AppConstants.RESTAURANT_OPENING_TIME to arguments?.get(AppConstants.RESTAURANT_OPENING_TIME)!!,
                        AppConstants.RESTAURANT_CLOSING_TIME to arguments?.get(AppConstants.RESTAURANT_CLOSING_TIME)!!,
                        AppConstants.RESTAURANT_ALWAYS_OPEN to arguments?.get(AppConstants.RESTAURANT_ALWAYS_OPEN)!!,
                        AppConstants.FROM_RESTAURANT_DETAIL to true
                    )

                    view?.findNavController()
                        ?.navigate(
                            R.id.action_restaurantDetailParent_to_scheduleOrder
                            , bundle
                        )
                }
            }
        } else if (item is RestaurantDetailNewModel.AllData.Data) {
            var bundle = bundleOf(
                AppConstants.CUISINE_ID to item.id,
                AppConstants.CUISINE_NAME to item.name,
                AppConstants.FILTER_ID to item.filter,
                AppConstants.RESTAURANT_ID to arguments?.get(AppConstants.RESTAURANT_ID).toString()
            )

            view?.findNavController()
                ?.navigate(
                    R.id.action_restaurantDetailParent_to_mealsList
                    , bundle
                )
        }
    }
}
