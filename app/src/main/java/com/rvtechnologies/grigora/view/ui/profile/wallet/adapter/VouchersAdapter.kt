package com.rvtechnologies.grigora.view.ui.profile.wallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.ReceivedVoucherModel
import com.rvtechnologies.grigora.model.SubIssueModel
import com.rvtechnologies.grigora.model.VoucherModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class VouchersAdapter(
    val list: ArrayList<ReceivedVoucherModel>, var iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<VouchersAdapter.MyView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_voucher,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_code: TextView = view.findViewById(R.id.tv_code)
        var tv_info: TextView = view.findViewById(R.id.tv_info)
        var tv_copy: TextView = view.findViewById(R.id.tv_copy)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        holder.tv_code.text = list[position].voucher_code
        holder.tv_info.text =
            list[position].amount + " " + holder.tv_info.context.getString(R.string.grigora_gift_voucher)
        holder.tv_code.text = list[position].voucher_code

        holder.tv_copy.setOnClickListener {
            iRecyclerItemClick.onItemClick(list[position])
        }


    }

}