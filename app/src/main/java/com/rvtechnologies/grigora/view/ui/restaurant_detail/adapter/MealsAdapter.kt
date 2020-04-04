package com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.FeaturedModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel

class MealsAdapter(
    val list: ArrayList<RestaurantDetailNewModel.MealItem>,
    val iRecyclerItemClick: IRecyclerItemClick
) : RecyclerView.Adapter<MealsAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_meal = view.findViewById<ImageView>(R.id.img_meal)
        var tv_name = view.findViewById<TextView>(R.id.tv_name)
        var tv_price = view.findViewById<TextView>(R.id.tv_price)
        var tv_rating = view.findViewById<TextView>(R.id.tv_rating)
        var tv_desc = view.findViewById<TextView>(R.id.tv_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_meal,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        CommonUtils.loadImage(holder.img_meal, list[position].image)
        holder.tv_name.text = list[position].name
        holder.tv_price.text = "₦ " + list[position].price
        holder.tv_rating.text = list[position].avg_ratings.toString()
        holder.tv_desc.text = list[position].description

        holder.itemView.setOnClickListener { iRecyclerItemClick.onItemClick(list[position]) }
    }

}