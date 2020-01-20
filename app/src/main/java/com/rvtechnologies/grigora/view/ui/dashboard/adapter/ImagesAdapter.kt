package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.paystack.android.utils.Utils
import com.facebook.shimmer.ShimmerFrameLayout
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class ImagesAdapter(
    val list: ArrayList<NewDashboardModel.Image>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ImageContainer(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_image,
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
     }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ImageContainer
        CommonUtils.loadImage(holder.img_data, list[position].image)
    }
}

