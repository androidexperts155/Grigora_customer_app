package com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.FeaturedModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel

class ParentsAdapter(
    val list: ArrayList<RestaurantDetailNewModel.AllData.Data>,
    val iRecyclerItemClick: IRecyclerItemClick
) : RecyclerView.Adapter<ParentsAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_count = view.findViewById<TextView>(R.id.tv_count)
        var tv_name = view.findViewById<TextView>(R.id.tv_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_category_parent,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_count.text = list[position].id.toString()
        holder.tv_name.text = list[position].name

        holder.itemView.setOnClickListener {
            iRecyclerItemClick.onItemClick(position)
        }
    }

}