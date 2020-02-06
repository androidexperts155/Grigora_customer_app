package com.rvtechnologies.grigora.view.ui.restaurant_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.DateModel
import com.rvtechnologies.grigora.model.models.AddressModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants

class DateAdapter(
    val list: ArrayList<DateModel>,
    val iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<DateAdapter.MyView>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.calender_date_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_name: TextView = view.findViewById(R.id.tv_name)
        var tv_day: TextView = view.findViewById(R.id.tv_day)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        holder.tv_name.text = CommonUtils.getFormattedTimeOrDate(
            list[position].date,
            "yyyy-MM-dd HH:mm:ss",
            "EEE"
        )

        holder.tv_day.text = CommonUtils.getFormattedTimeOrDate(
            list[position].date,
            "yyyy-MM-dd HH:mm:ss",
            "dd"
        )


        if (list[position].selected) {
            holder.tv_day.setBackgroundResource(R.drawable.date_selected_bg)
            holder.tv_day.setTextColor(ContextCompat.getColor(holder.tv_day.context, R.color.white))
        } else {
            holder.tv_day.setBackgroundResource(R.drawable.date_deselected_bg)
            if (CommonUtils.isDarkMode())
                holder.tv_day.setTextColor(
                    ContextCompat.getColor(
                        holder.tv_day.context,
                        R.color.white
                    )
                )
            else
                holder.tv_day.setTextColor(
                    ContextCompat.getColor(
                        holder.tv_day.context,
                        R.color.textBlack
                    )
                )
        }

        holder.itemView.setOnClickListener {
            iRecyclerItemClick.onItemClick(position)
        }
    }


}