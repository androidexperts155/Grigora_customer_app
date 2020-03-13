package com.rvtechnologies.grigora.view.ui.profile.wallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.SubIssueModel
import com.rvtechnologies.grigora.model.VoucherModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class PriceAdapter(
    val list: ArrayList<VoucherModel>, var iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<PriceAdapter.MyView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_price,
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
        holder.tv_item.text = "₦ " + list[position].amount.toString()
        holder.itemView.setOnClickListener {
            iRecyclerItemClick.onItemClick(list[position])
        }

    }

}