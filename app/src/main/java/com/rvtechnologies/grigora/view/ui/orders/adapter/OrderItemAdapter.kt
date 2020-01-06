package com.rvtechnologies.grigora.view.ui.orders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.OrderItemModel

class OrderItemAdapter(val list: ArrayList<OrderItemModel.OrderDetail>) :
    RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.order_detail_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.tv_name.text = list[position].quantity.toString() + "x " + list[position].itemName
        holder.tv_price.text = "₦ " + list[position].price
        var choices = ""
        if (list[position].itemChoices.size > 0) {
            for (item in list[position].itemChoices) {
                choices = choices + ", " + item.name
            }
        }

        if (!choices.isNullOrEmpty())
            choices.replaceFirst(",", "")

        holder.tv_choices.text = choices
    }

    inner class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tv_name: TextView = view.findViewById(R.id.tv_name)
        var tv_choices: TextView = view.findViewById(R.id.tv_choices)
        var tv_price: TextView = view.findViewById(R.id.tv_price)

    }

}