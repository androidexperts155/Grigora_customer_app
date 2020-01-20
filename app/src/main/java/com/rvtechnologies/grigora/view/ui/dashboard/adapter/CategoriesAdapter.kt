package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class CategoriesAdapter(
    val list: ArrayList<NewDashboardModel.Cuisine>
) :
    RecyclerView.Adapter<CategoriesAdapter.MyView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_category,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var img_data: ImageView = view.findViewById(R.id.img_data)
        var tv_data: TextView = view.findViewById(R.id.tv_data)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        holder.tv_data.text = list[position].name

        if (list[position].selected) {
            holder.tv_data.setTextColor(
                ContextCompat.getColor(
                    holder.tv_data.context,
                    R.color.colorPrimaryDark
                )
            )
            CommonUtils.loadImage(holder.img_data, list[position].background_icon)

        } else {
            CommonUtils.loadImage(holder.img_data, list[position].icon)

            if (CommonUtils.isDarkMode()) {
                holder.tv_data.setTextColor(
                    ContextCompat.getColor(
                        holder.tv_data.context,
                        R.color.white
                    )
                )
            } else {
                holder.tv_data.setTextColor(
                    ContextCompat.getColor(
                        holder.tv_data.context,
                        R.color.textBlack
                    )
                )
            }
        }

    }


}

