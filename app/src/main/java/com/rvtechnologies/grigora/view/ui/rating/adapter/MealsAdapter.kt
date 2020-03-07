package com.rvtechnologies.grigora.view.ui.rating.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.OrderItemModel

class MealsAdapter(
    val mealsToRate: ArrayList<OrderItemModel.OrderDetail>,
    val ratingChanged: DataChanged
) :
    RecyclerView.Adapter<MealsAdapter.MealItemViewHolder>() {

    inner class MealItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_name: TextView = view.findViewById(R.id.tv_name)
        var rt_rating: RatingBar = view.findViewById(R.id.rt_rating)
        var ed_review: EditText = view.findViewById(R.id.ed_review)
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

        holder.ed_review.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                ratingChanged.reviewChanged(position, s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    interface DataChanged {
        fun ratingChanged(position: Int, rating: Float)
        fun reviewChanged(position: Int, review: String)
    }


}