package com.rvtechnologies.grigora.view.ui.addresses.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.AddressModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants

class AddressesAdapter(
    val list: ArrayList<AddressModel>,
    val iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<AddressesAdapter.MyView>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_address,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_type: TextView = view.findViewById(R.id.tv_type)
        var tv_location: TextView = view.findViewById(R.id.tv_location)
        var tv_apt_number: TextView = view.findViewById(R.id.tv_apt_number)
        var img_check: ImageView = view.findViewById(R.id.img_check)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
        holder.tv_type.text = list[position].locationTypeName
        holder.tv_location.text = list[position].address
        holder.tv_apt_number.text = list[position].completeAddress

        if (CommonUtils.getPrefValue(
                holder.itemView.context,
                PrefConstants.ADDRESS_ID
            ).equals(list[position].id.toString())
        ) {
            holder.tv_type.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorPrimaryDark
                )
            )
            holder.tv_location.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorPrimaryDark
                )
            )
            holder.tv_apt_number.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorPrimary
                )
            )
            holder.img_check.visibility = View.VISIBLE
        } else {
            holder.img_check.visibility = View.GONE
            holder.tv_type.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.textBlack
                )
            )
            holder.tv_location.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.textBlack
                )
            )
            holder.tv_apt_number.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.textGrey
                )
            )
        }

        holder.itemView.setOnClickListener {
            iRecyclerItemClick.onItemClick(list[position])
        }
    }

}