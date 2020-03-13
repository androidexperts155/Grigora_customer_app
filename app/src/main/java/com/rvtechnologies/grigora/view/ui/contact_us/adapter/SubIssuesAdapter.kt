package com.rvtechnologies.grigora.view.ui.contact_us.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.FaqModel
import com.rvtechnologies.grigora.model.SubIssueModel
import com.rvtechnologies.grigora.model.models.AddressModel
import com.rvtechnologies.grigora.model.models.ChipModel
import com.rvtechnologies.grigora.model.models.LocationTypeModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants

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