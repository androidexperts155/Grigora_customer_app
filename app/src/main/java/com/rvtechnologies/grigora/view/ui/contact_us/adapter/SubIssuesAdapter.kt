package com.rvtechnologies.grigora.view.ui.contact_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.SubIssueModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class SubIssuesAdapter(
    val list: ArrayList<SubIssueModel>, var iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<SubIssuesAdapter.MyView>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_subissue,
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
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        holder.tv_issue.text = list[position].name
        holder.itemView.setOnClickListener {
            iRecyclerItemClick.onItemClick(list[position])
        }

    }

}