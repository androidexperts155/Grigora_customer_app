package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class FilterAdapter(
    val list: ArrayList<NewDashboardModel.Filter>,
    val iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val RATING = 1
    val NORMAL = 2


    override fun getItemViewType(position: Int): Int {
        return if (list[position].selectionType == "2" || list[position].selectionType == "1")
            RATING
        else
            NORMAL
    }


    inner class RatingView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_filter_name: TextView = view.findViewById(R.id.tv_filter_name)
        var img_arrow: ImageView = view.findViewById(R.id.img_arrow)
        var view: View = view.findViewById(R.id.view)
        var li_main: LinearLayout = view.findViewById(R.id.li_main)
    }

    inner class NormalView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_filter_name: TextView = view.findViewById(R.id.tv_filter_name)
        var li_main: LinearLayout = view.findViewById(R.id.li_main)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == RATING) {
            return RatingView(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.rating_filter,
                    parent,
                    false
                )
            )
        } else {
            return NormalView(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.filter_normal,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RatingView) {
            if (list[position].selected) {
                holder.li_main.setBackgroundResource(R.drawable.filter_selected_bg)

                holder.tv_filter_name.setTextColor(
                    ContextCompat.getColor(
                        holder.view.context,
                        R.color.white
                    )
                )
                holder.img_arrow.setColorFilter(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )

                holder.view.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.view.context,
                        R.color.white
                    )
                )

            } else {
                holder.li_main.setBackgroundResource(R.drawable.filter_deselected_bg)
                if (CommonUtils.isDarkMode()) {
                    holder.tv_filter_name.setTextColor(
                        ContextCompat.getColor(
                            holder.view.context,
                            R.color.white
                        )
                    )
                    holder.img_arrow.setColorFilter(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.white
                        )
                    )

                    holder.view.setBackgroundColor(
                        ContextCompat.getColor(
                            holder.view.context,
                            R.color.white
                        )
                    )
                } else {
                    holder.tv_filter_name.setTextColor(
                        ContextCompat.getColor(
                            holder.view.context,
                            R.color.textBlack
                        )
                    )
                    holder.img_arrow.setColorFilter(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.textBlack
                        )
                    )

                    holder.view.setBackgroundColor(
                        ContextCompat.getColor(
                            holder.view.context,
                            R.color.textBlack
                        )
                    )
//                    set white
                }
            }


            if (list[position].selectionType == "2") {
                if (!list[position].multiSelected.isNullOrEmpty()) {
                    var name =  holder.tv_filter_name.context.getString(R.string.price)
//                    if (list[position].multiSelected.contains("1")) {
//                        name = "₦"
//                    }
//
//                    if (list[position].multiSelected.contains("2")) {
//                        if (name != "")
//                            name += ",₦₦"
//                        else
//                            name += "₦₦"
//                    }
//
//                    if (list[position].multiSelected.contains("3")) {
//                        if (name != "")
//                            name += ",₦₦₦"
//                        else
//                            name += "₦₦₦"
//                    }
//
//                    if (list[position].multiSelected.contains("4")) {
//                        if (name != "")
//                            name += ",₦₦₦₦"
//                        else
//                            name += "₦₦₦₦"
//                    }
                    holder.tv_filter_name.text = name
                } else
                    holder.tv_filter_name.text = list[position].name

            } else {
                holder.tv_filter_name.text = list[position].name

            }


            holder.img_arrow.setOnClickListener {
                list[position].arrowClicked = true
                iRecyclerItemClick.onItemClick(list[position])
            }
            holder.itemView.setOnClickListener {
                list[position].arrowClicked = false
                iRecyclerItemClick.onItemClick(list[position])
            }
        } else {
            holder as NormalView
            holder.tv_filter_name.text = list[position].name

            if (list[position].selected) {
                holder.li_main.setBackgroundResource(R.drawable.filter_selected_bg)
                holder.tv_filter_name.setTextColor(
                    ContextCompat.getColor(
                        holder.tv_filter_name.context,
                        R.color.white
                    )
                )
            } else {
                holder.li_main.setBackgroundResource(R.drawable.filter_deselected_bg)
                if (CommonUtils.isDarkMode()) {
                    holder.tv_filter_name.setTextColor(
                        ContextCompat.getColor(
                            holder.tv_filter_name.context,
                            R.color.white
                        )
                    )
                } else {
                    holder.tv_filter_name.setTextColor(
                        ContextCompat.getColor(
                            holder.tv_filter_name.context,
                            R.color.textBlack
                        )
                    )
                }

            }
            holder.itemView.setOnClickListener { iRecyclerItemClick.onItemClick(list[position]) }
        }


    }

}