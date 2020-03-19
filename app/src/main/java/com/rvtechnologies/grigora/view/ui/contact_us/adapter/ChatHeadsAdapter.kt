package com.rvtechnologies.grigora.view.ui.contact_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.ChatHeadModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class ChatHeadsAdapter(
    val list: ArrayList<ChatHeadModel>, var iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<ChatHeadsAdapter.MyView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_chat_head,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_issue: TextView = view.findViewById(R.id.tv_issue)
        var tv_id: TextView = view.findViewById(R.id.tv_id)
        var tv_date: TextView = view.findViewById(R.id.tv_date)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        holder.tv_issue.text = list[position].issue_name
        holder.tv_id.text =
            holder.tv_id.context.getString(R.string.ticket_id) + " " + list[position].ticket_id
        holder.tv_date.text = CommonUtils.getFormattedUtc(list[position].updated_at,"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","yyyy-MM-dd")


        holder.itemView.setOnClickListener {
            iRecyclerItemClick.onItemClick(list[position])
        }

    }

}