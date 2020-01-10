package com.rvtechnologies.grigora.view.ui.login_signup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.rvtechnologies.grigora.R
import com.smarteist.autoimageslider.SliderViewAdapter


//Earn points, Get Free Delivery
//
//
//Group Delivery
//
//
class SliderAdapter : SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {
    var imagesList = ArrayList<Int>()
    var titleList = ArrayList<String>()
    var descList = ArrayList<String>()

    init {
        imagesList.add(R.drawable.img_reserve)
        imagesList.add(R.drawable.fav_restaurant)
        imagesList.add(R.drawable.img_earn)
        imagesList.add(R.drawable.live_traking)

        titleList.add("Advance Table Bookings")
        titleList.add("Fast Delivery from Favorite Restaurants")
        titleList.add("Earn points, Get Free Delivery")
        titleList.add("Group Delivery")


        descList.add("Reserve tables &amp; make bookings at your favorite restaurant")
        descList.add("Explore the extensive selection of your favorite restaurants and cuisines")
        descList.add("Get points or discounts from previous deliveries")
        descList.add("Order meals together with family, friends and colleagues")
    }

    class SliderAdapterVH(itemView: View) :
        SliderViewAdapter.ViewHolder(itemView) {

        var imageView: ImageView = itemView.findViewById(R.id.image_view)
        var tv_title: TextView? = itemView.findViewById(R.id.tv_title)
        var tv_desc: TextView? = itemView.findViewById(R.id.tv_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val inflate =
            LayoutInflater.from(parent!!.context).inflate(R.layout.item_image_slider, null)
        return SliderAdapterVH(
            inflate
        )
    }

    override fun getCount(): Int {
        return 4
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {
        viewHolder!!.imageView.setImageResource(imagesList[position])
        viewHolder!!.tv_title?.text = titleList[position]
        viewHolder!!.tv_desc?.text = descList[position]
    }
}