package com.rvtechnologies.grigora.view.ui.restaurant_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.RestaurantDetailModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.OnItemClickListener
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicks

class RestaurantDetailAdapter(
    val list: ArrayList<RestaurantDetailModel.AllData>,
    val onItemClickListener: OnItemClickListener,
    val quantityClicks: QuantityClicks
) : RecyclerView.Adapter<RestaurantDetailAdapter.DetailView>() {

    inner class DetailView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_title: TextView = view.findViewById(R.id.tv_title)
        var tv_count: TextView = view.findViewById(R.id.tv_count)
        var rc_items: RecyclerView = view.findViewById(R.id.rc_items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailView {
        return DetailView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_restaurant_details_meals,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DetailView, position: Int) {
        holder.tv_count.text = list[position].items.size.toString()
        holder.tv_title.text = list[position].name
        var adapter =
            RestaurantItemAdapter(list[position].items, onItemClickListener, quantityClicks,position)

        holder.rc_items.adapter = adapter


    }


}