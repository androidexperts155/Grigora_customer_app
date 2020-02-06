package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants

class DashboardAllRestaurantAdapter(
    val list: ArrayList<NewDashboardModel.AllRestautants>,
    val minKiloMeter: String,
    val baseDeliveryFee: String,
    val iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<DashboardAllRestaurantAdapter.MyView>() {

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
        var rc_images: RecyclerView = view.findViewById(R.id.rc_images)
        var tv_name: TextView = view.findViewById(R.id.tv_name)
        var tv_delivery_time: TextView = view.findViewById(R.id.tv_delivery_time)
        var tv_rating: TextView = view.findViewById(R.id.tv_rating)
        var tv_delivery_charges: TextView = view.findViewById(R.id.tv_delivery_charges)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        var detail = list[position]
        holder.tv_name.text = detail.name

        if (detail.items.isNotEmpty()) {
            holder.rc_images.adapter = ImagesAdapter(detail.items)
            holder.rc_images.setOnClickListener {
                iRecyclerItemClick.onItemClick(list[position])
            }
        }

        holder.tv_rating.text = detail.averageRating
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
            iRecyclerItemClick.onItemClick(list[position])
        }
    }


}

