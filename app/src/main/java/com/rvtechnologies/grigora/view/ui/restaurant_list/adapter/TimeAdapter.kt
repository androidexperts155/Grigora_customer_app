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

class TimeAdapter(
    val list: ArrayList<String>,
    val iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<TimeAdapter.MyView>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_time,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_item: TextView = view.findViewById(R.id.tv_item)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {

        holder.tv_item.text = list[position]

        holder.itemView.setOnClickListener {
            iRecyclerItemClick.onItemClick(list[position])
        }
    }


}