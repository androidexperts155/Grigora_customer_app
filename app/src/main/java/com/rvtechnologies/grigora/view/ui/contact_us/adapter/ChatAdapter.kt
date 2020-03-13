package com.rvtechnologies.grigora.view.ui.contact_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.MessageModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants

class ChatAdapter(
    val list: ArrayList<MessageModel>
) :
    RecyclerView.Adapter<ChatAdapter.MyView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_messages,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_sent: TextView = view.findViewById(R.id.tv_sent)
        var tv_received: TextView = view.findViewById(R.id.tv_received)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        var model = list[position]
        if (model.sender_id.equals(
                CommonUtils.getPrefValue(
                    holder.tv_sent.context!!,
                    PrefConstants.ID
                ).toInt()
            )
        ) {
            holder.tv_sent.text = model.message

            holder.tv_sent.visibility = View.VISIBLE
            holder.tv_received.visibility = View.GONE

        } else {
            holder.tv_received.text = model.message

            holder.tv_sent.visibility = View.GONE
            holder.tv_received.visibility = View.VISIBLE
        }
    }
}