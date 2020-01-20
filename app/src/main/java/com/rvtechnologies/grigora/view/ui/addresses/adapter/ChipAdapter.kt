package com.rvtechnologies.grigora.view.ui.addresses.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.AddressModel
import com.rvtechnologies.grigora.model.models.ChipModel
import com.rvtechnologies.grigora.model.models.LocationTypeModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants

class ChipAdapter(
    val list: ArrayList<LocationTypeModel>,
    val iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<ChipAdapter.MyView>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_chip,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_chip: TextView = view.findViewById(R.id.tv_chip)
        var li_main: LinearLayout = view.findViewById(R.id.li_main)
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {

        holder.tv_chip.text = list[position].name

        if (list[position].selected) {
            holder.li_main.background =
                ContextCompat.getDrawable(holder.li_main.context, R.drawable.chip_selected_bg)
            holder.tv_chip.setTextColor(
                ContextCompat.getColor(
                    holder.li_main.context,
                    R.color.white
                )
            )

        } else {
            holder.li_main.background =
                ContextCompat.getDrawable(holder.li_main.context, R.drawable.chip_deselected_bg)
            if (CommonUtils.isDarkMode()) {
                holder.tv_chip.setTextColor(
                    ContextCompat.getColor(
                        holder.li_main.context,
                        R.color.lightGrey
                    )
                )
            } else
                holder.tv_chip.setTextColor(
                    ContextCompat.getColor(
                        holder.li_main.context,
                        R.color.text_hint_color
                    )
                )
        }

        holder.itemView.setOnClickListener {
            iRecyclerItemClick.onItemClick(position)
        }
    }

}