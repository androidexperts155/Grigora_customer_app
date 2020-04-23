package com.rvtechnologies.grigora.view.ui.orders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.utils.CommonUtils

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
        holder.tv_name.text = list[position].itemName + " x " + list[position].quantity.toString()
        var price = list[position].price.toDouble()


        if (!list[position].itemChoices.isNullOrEmpty()) {
            for (item in list[position].itemChoices) {
                if (!item.itemSubCategory.isNullOrEmpty()) {
                    for (sub in item.itemSubCategory) {

                        if (!sub.addOnPrice.isNullOrEmpty())
                            price += sub.addOnPrice.toDouble()

                        if (!sub.item_sub_sub_category.isNullOrEmpty()) {
                            for (subsub in sub.item_sub_sub_category) {
                                price += subsub.add_on_price
                            }
                        }
                    }
                }
                price *= list[position].quantity
            }
        }

        holder.tv_price.text = "₦ " + CommonUtils.getRoundedOff(price)
        var choices = ""
        if (list[position].itemChoices.size > 0) {
            for (item in list[position].itemChoices) {
                choices = choices + ", " + item.name
            }
        }

        if (!choices.isNullOrEmpty())
            choices.replaceFirst(",", "")

//        R.id.rd_veg -> {
//            filter = "1"
//        }
//        R.id.rd_nonveg -> {
//            filter = "0"
//        }
//        R.id.rd_containsegg -> {
//            filter = "2"
//        }

//        don't show choices, required by client
        choices = ""
        if (choices.isNullOrEmpty())
            holder.tv_choices.visibility = View.GONE


        when (list[position].pureVeg) {
            "1" -> {
                holder.img_type.setImageResource(R.drawable.veg)
            }
            "2" -> {
                holder.img_type.setImageResource(R.drawable.containes_egg)
            }
            "0" -> {
                holder.img_type.setImageResource(R.drawable.non_veg)
            }
        }

        holder.tv_choices.text = choices
    }

    inner class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tv_name: TextView = view.findViewById(R.id.tv_name)
        var tv_choices: TextView = view.findViewById(R.id.tv_choices)
        var tv_price: TextView = view.findViewById(R.id.tv_price)
        var img_type: ImageView = view.findViewById(R.id.img_type)

    }

}