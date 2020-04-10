package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class DashboardRestaurantAdapter(
    val list: ArrayList<NewDashboardModel.CustomizedData.Restaurant>,
    val minKiloMeter: String,
    val baseDeliveryFee: String,
    val iRecyclerItemClick: IRecyclerItemClick,
    val uiType: String
) :
    RecyclerView.Adapter<DashboardRestaurantAdapter.MyView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_dashboard_rest_vertical_with_image,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_name: TextView = view.findViewById(R.id.tv_name)
        var tv_delivery_time: TextView = view.findViewById(R.id.tv_delivery_time)
        var tv_rating: TextView = view.findViewById(R.id.tv_rating)
        var tv_delivery_charges: TextView = view.findViewById(R.id.tv_delivery_charges)
        var tv_closed: TextView = view.findViewById(R.id.tv_closed)
        var vp: ViewPager = view.findViewById(R.id.vp)
        var dots_indicator: DotsIndicator = view.findViewById(R.id.dots_indicator)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        var detail = list[position]
//        CommonUtils.loadImage(holder.img_data, detail.image)

        if (!detail.items.isNullOrEmpty()) {
            detail.uiTpe = uiType
            holder.vp.adapter =
                RestImagePagerAdapter(holder.itemView.context!!, detail, iRecyclerItemClick)

            holder.dots_indicator.setViewPager(holder.vp)
            holder.vp.setOnClickListener {
                list[position].uiTpe = uiType

                iRecyclerItemClick.onItemClick(list[position])
            }


//            holder.rc_images.adapter = ImagesAdapter(detail, iRecyclerItemClick)
//            holder.rc_images.setOnClickListener {
//                iRecyclerItemClick.onItemClick(list[position])
//            }
        }


        holder.tv_name.text = detail.name

        holder.tv_rating.text = detail.averageRating.toString()
        holder.tv_delivery_time.text =
            detail.preparing_time + " " + holder.tv_delivery_time.context.getString(R.string.min)

        var distance = CommonUtils.calculateDistance(
            detail.latitude.toDouble(),
            detail.longitude.toDouble(),
            CommonUtils.getPrefValue(holder.tv_name.context, PrefConstants.LATITUDE).toDouble(),
            CommonUtils.getPrefValue(holder.tv_name.context, PrefConstants.LONGITUDE).toDouble()
        )

        var price = baseDeliveryFee.toFloat() + (distance * minKiloMeter.toFloat())
        holder.tv_delivery_charges.text =
            "₦" + (price.toInt()).toString() + " " + holder.tv_delivery_time.context.getString(R.string.delivery)


        holder.itemView.setOnClickListener {
            list[position].uiTpe = uiType
            iRecyclerItemClick.onItemClick(list[position])
        }

        holder.tv_closed.visibility = View.GONE
        holder.tv_delivery_charges.visibility = View.VISIBLE

        if (detail.fullTime == "0") {
            if (!CommonUtils.isRestaurantOpen(
                    detail.openingTime,
                    detail.closingTime
                )
            ) {
                holder.tv_closed.visibility = View.VISIBLE
                holder.tv_delivery_charges.visibility = View.GONE
                holder.tv_closed.text = holder.tv_closed.context!!.getString(R.string.closed)
            }
        }
        if ((CommonUtils.isRestaurantOpen(
                detail.openingTime,
                detail.closingTime
            ) || detail.fullTime == "1") && detail.busyStatus == "1"
        ) {
            holder.tv_closed.visibility = View.VISIBLE
            holder.tv_delivery_charges.visibility = View.GONE
            holder.tv_closed.text = holder.tv_closed.context!!.getString(R.string.busy)
        }
    }


}

