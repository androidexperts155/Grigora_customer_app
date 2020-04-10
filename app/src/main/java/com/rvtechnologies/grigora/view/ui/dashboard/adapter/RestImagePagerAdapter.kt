package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class RestImagePagerAdapter(
    val context: Context,
    private val detail: NewDashboardModel.CustomizedData.Restaurant,
    val iRecyclerItemClick: IRecyclerItemClick
) : PagerAdapter() {

    override fun getCount(): Int {
        return detail.items.size
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val layout =
            inflater.inflate(R.layout.item_image_pager, container, false) as ViewGroup


        var img_data = layout.findViewById<ImageView>(R.id.img_data)

        img_data.setOnClickListener { iRecyclerItemClick.onItemClick(detail) }

        CommonUtils.loadImage(img_data!!, detail.items[position].image)

        container.addView(layout)
        return layout
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }
}