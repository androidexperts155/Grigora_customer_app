package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class TopBrandAdapter(
    val list: ArrayList<NewDashboardModel.CustomizedData.Restaurant>,
    val minKiloMeter: String,
    val baseDeliveryFee: String,
    val iRecyclerItemClick: IRecyclerItemClick,
    val uiType: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ImageContainer(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_top_brand,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ImageContainer(view: View) : RecyclerView.ViewHolder(view) {
        var img_data: ImageView = view.findViewById(R.id.img_data)
        var tv_name: TextView = view.findViewById(R.id.tv_name)
        var tv_delivery_time: TextView = view.findViewById(R.id.tv_delivery_time)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ImageContainer
        CommonUtils.loadImage(holder.img_data, list[position].image)
        holder.tv_name.text = list[position].name

        holder.tv_delivery_time.text =
            list[position].preparing_time + " " + holder.tv_delivery_time.context.getString(R.string.min)

        holder.itemView.setOnClickListener{
            list[position].uiTpe=uiType
            iRecyclerItemClick.onItemClick(list[position])
        }

    }
}

