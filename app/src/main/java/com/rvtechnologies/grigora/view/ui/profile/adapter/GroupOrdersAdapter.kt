package com.rvtechnologies.grigora.view.ui.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.GroupOrdersModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class GroupOrdersAdapter(
    val list: ArrayList<GroupOrdersModel>,val iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<GroupOrdersAdapter.MyView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_group_orders,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_rest_name: TextView = view.findViewById(R.id.tv_rest_name)
        var tv_money: TextView = view.findViewById(R.id.tv_money)
        var tv_last: TextView = view.findViewById(R.id.tv_last)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        holder.tv_rest_name.text = list[position].restaurant_name
        holder.tv_money.text = "₦ " + list[position].total_price
        holder.tv_last.text =
            "${list[position].cart_items_count} ${holder.itemView.context!!.getString(R.string.items)} . ${list[position].cart_users_count} ${holder.itemView.context!!.getString(
                R.string.participants
            )}"


        holder.itemView.setOnClickListener {
            iRecyclerItemClick.onItemClick(position)
        }

    }

}