package com.rvtechnologies.grigora.view.ui.login_signup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.rvtechnologies.grigora.R


class ViewPagerAdapter(
var context: Context
) : PagerAdapter() {
    var imagesList = ArrayList<Int>()
    var titleList = ArrayList<String>()
    var descList = ArrayList<String>()

    init {
        imagesList.add(R.drawable.img_reserve)
        imagesList.add(R.drawable.fav_restaurant)
        imagesList.add(R.drawable.img_earn)
        imagesList.add(R.drawable.live_traking)
        imagesList.add(R.drawable.img_group_delivery)


        titleList.add("Seamless Delivery")
        titleList.add("Track your order easily to your door step in real time")
        titleList.add("Fast Delivery from Favorite Restaurants")
        titleList.add("Earn points, Get Free Delivery")
        titleList.add("Group Delivery")


        descList.add("Track your order easily to your door step in real time")
        descList.add("Reserve tables &amp; make bookings at your favorite restaurant")
        descList.add("Explore the extensive selection of your favorite restaurants and cuisines")
        descList.add("Get points or discounts from previous deliveries")
        descList.add("Order meals together with family, friends and colleagues")
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
         val inflater = LayoutInflater.from(context)
        val layout =
            inflater.inflate(R.layout.item_image_slider, container, false) as ViewGroup


        var img = layout.findViewById<ImageView>(R.id.image_view)
        var tv_title = layout.findViewById<TextView>(R.id.tv_title)
        var tv_desc = layout.findViewById<TextView>(R.id.tv_desc)

        img.setImageResource(imagesList[position])
        tv_title.text = titleList[position]
        tv_desc.text = descList[position]




        container.addView(layout)


        return layout
//        val inflater = LayoutInflater.from(container.context)




//        val view = LayoutInflater.from(container!!.context)
//            .inflate(R.layout.item_image_slider, container,false)
//
//        var img = view.findViewById<ImageView>(R.id.image_view)
//        var tv_title = view.findViewById<TextView>(R.id.tv_title)
//        var tv_desc = view.findViewById<TextView>(R.id.tv_desc)
//
//        img.setImageResource(imagesList[position])
//        tv_title.text = titleList[position]
//        tv_desc.text = descList[position]
//
//        container.addView(view)
//        return container
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return imagesList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)


    }
}