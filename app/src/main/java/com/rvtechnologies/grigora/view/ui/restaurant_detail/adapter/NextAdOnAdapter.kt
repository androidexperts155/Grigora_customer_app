package com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel

class NextAdOnAdapter(var list: ArrayList<RestaurantDetailNewModel.ItemSubSubCategory>,var selected:Selected) :
    RecyclerView.Adapter<NextAdOnAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_selected = view.findViewById<ImageView>(R.id.img_selected)
        var img_desel = view.findViewById<ImageView>(R.id.img_desel)
        var tv_name = view.findViewById<TextView>(R.id.tv_name)
        var tv_price = view.findViewById<TextView>(R.id.tv_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent?.context)
                .inflate(R.layout.item_inner_adon, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_name.text = list[position].name
        holder.tv_price.text =
            "(+" + holder.itemView.context.getString(R.string.currency_sym) + list[position].add_on_price + ")"

        holder.itemView.setOnClickListener {
            list[position].checked = !list[position].checked
            if (list[position].checked) {
                holder.img_selected.visibility = View.VISIBLE
                holder.img_desel.visibility = View.GONE
                holder.tv_name.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context!!,
                        R.color.colorPrimaryDark
                    )
                )
                holder.tv_price.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context!!,
                        R.color.colorPrimaryDark
                    )
                )

                selected.isSelected(true,position)
            } else {
                holder.img_selected.visibility = View.GONE
                holder.img_desel.visibility = View.VISIBLE

                if (CommonUtils.isDarkMode()) {
                    holder.tv_name.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context!!,
                            R.color.white
                        )
                    )
                    holder.tv_price.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context!!,
                            R.color.white
                        )
                    )
                } else {
                    holder.tv_name.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context!!,
                            R.color.textBlack
                        )
                    )
                    holder.tv_price.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context!!,
                            R.color.textBlack
                        )
                    )
                }
                selected.isSelected(false,position)


            }

        }
    }


    interface  Selected{
        fun isSelected(selected:Boolean,position: Int)
    }

}