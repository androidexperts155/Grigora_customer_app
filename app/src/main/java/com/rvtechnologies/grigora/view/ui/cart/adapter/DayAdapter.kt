package com.rvtechnologies.grigora.view.ui.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.DayModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class DayAdapter(var list: ArrayList<DayModel>, var iRecyclerItemClick: IRecyclerItemClick) :
    RecyclerView.Adapter<DayAdapter.DayView>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayView {
        return DayView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_day,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DayView, position: Int) {
        holder.tv_name.text = list[position].name
        holder.tv_date.text = list[position].date
        if (list[position].selected) {
            holder.tv_name.setTextColor(
                ContextCompat.getColor(
                    holder.tv_date.context!!,
                    R.color.colorPrimaryDark
                )
            )

            holder.tv_date.setTextColor(
                ContextCompat.getColor(
                    holder.tv_date.context!!,
                    R.color.colorPrimaryDark
                )
            )
            holder.li_main.setBackgroundResource(R.drawable.date_selected)
        } else {
            if (CommonUtils.isDarkMode()) {
                holder.tv_name.setTextColor(
                    ContextCompat.getColor(
                        holder.tv_date.context!!,
                        R.color.white
                    )
                )

                holder.tv_date.setTextColor(
                    ContextCompat.getColor(
                        holder.tv_date.context!!,
                        R.color.white
                    )
                )
            } else {
                holder.tv_name.setTextColor(
                    ContextCompat.getColor(
                        holder.tv_date.context!!,
                        R.color.textBlack
                    )
                )

                holder.tv_date.setTextColor(
                    ContextCompat.getColor(
                        holder.tv_date.context!!,
                        R.color.textBlack
                    )
                )
            }

            holder.li_main.setBackgroundResource(R.drawable.date_de_selected)
        }

        holder.li_main.setOnClickListener { iRecyclerItemClick.onItemClick(position.toString()) }
    }

    inner class DayView(var view: View) : RecyclerView.ViewHolder(view) {
        var li_main = view.findViewById<LinearLayout>(R.id.li_main)
        var tv_date = view.findViewById<TextView>(R.id.tv_date)
        var tv_name = view.findViewById<TextView>(R.id.tv_name)
    }

}

