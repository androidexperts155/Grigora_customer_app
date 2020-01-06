package com.rvtechnologies.grigora.view.ui.rating.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.OrderItemModel

    class MealsAdapter(
    val mealsToRate: ArrayList<OrderItemModel.OrderDetail>,
    val ratingChanged: RatingChanged
) :
    RecyclerView.Adapter<MealsAdapter.MealItemViewHolder>() {

    inner class MealItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_name: TextView = view.findViewById(R.id.tv_name)
        var rt_rating: RatingBar = view.findViewById(R.id.rt_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealItemViewHolder {
        return MealItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rating_meal_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mealsToRate.size

    }

    override fun onBindViewHolder(holder: MealItemViewHolder, position: Int) {
        holder.tv_name.text = mealsToRate[position].itemName
        holder.rt_rating.setOnRatingBarChangeListener { ratingBar, fl, b ->
            ratingChanged.ratingChanged(position, fl)
        }
    }

    interface RatingChanged {
        fun ratingChanged(position: Int, rating: Float)
    }
}