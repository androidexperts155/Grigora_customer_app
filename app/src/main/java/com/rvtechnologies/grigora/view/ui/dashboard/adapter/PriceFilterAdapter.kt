package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.paystack.android.utils.Utils
import com.facebook.shimmer.ShimmerFrameLayout
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.FilteredPrice
import com.rvtechnologies.grigora.model.PriceFilterModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class PriceFilterAdapter(
    val item: FilteredPrice, val iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return PriceContainer(
            LayoutInflater.from(parent.context).inflate(
                R.layout.price_item_filter,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return item.list.size
    }

    inner class PriceContainer(view: View) : RecyclerView.ViewHolder(view) {
        var li_main: LinearLayout = view.findViewById(R.id.li_main)
        var tv_selection: TextView = view.findViewById(R.id.tv_selection)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as PriceContainer
        holder.tv_selection.text = item.list[position].name
        if (item.list[position].selected) {
            holder.li_main.background
                .setColorFilter(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorPrimaryDark
                    ), PorterDuff.Mode.SRC_ATOP
                )



            if (CommonUtils.isDarkMode()) {
                holder.tv_selection.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )

                holder.tv_selection.background
                    .setColorFilter(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.background_dark_light
                        ), PorterDuff.Mode.SRC_ATOP
                    )
            } else {
                holder.tv_selection.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.textBlack
                    )
                )
                holder.tv_selection.background
                    .setColorFilter(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.white
                        ), PorterDuff.Mode.SRC_ATOP
                    )

            }


        } else {
            if (CommonUtils.isDarkMode()) {
                holder.li_main.background
                    .setColorFilter(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.lightGrey
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                holder.tv_selection.background
                    .setColorFilter(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.background_dark
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                holder.tv_selection.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
            } else {
                holder.li_main.background
                    .setColorFilter(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.lightGrey
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                holder.tv_selection.background
                    .setColorFilter(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.white
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                holder.tv_selection.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.textBlack
                    )
                )
            }
        }

        holder.li_main.setOnClickListener {
            iRecyclerItemClick.onItemClick(position)
        }
    }
}

