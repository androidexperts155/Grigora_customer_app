package com.rvtechnologies.grigora.view.ui.restaurant_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.RestaurantViewBinding
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.model.models.RestaurantModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.ImagesAdapter


class RestaurantAdapter(
    var restaurantList: ArrayList<NewDashboardModel.AllRestautants>,
    val iRecyclerItemClick: IRecyclerItemClick
) : RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resModel = restaurantList[position]
        resModel.reviewToShow =
            "(${resModel.reviews} ${holder.itemView.context!!.getString(R.string.reviews)})"



        if ((resModel.fullTime == "0")) {
            resModel.closeToShow =
                "${holder.itemView.context!!.getString(R.string.closes)} ${CommonUtils.getFormattedTimeOrDate(
                    resModel.closingTime,
                    "HH:mm:ss",
                    "hh:mm aa"
                )}"
        } else {
            resModel.closeToShow = holder.itemView.context!!.getString(R.string.open_24_hours)
        }


        holder.binding.rcImages.adapter = ImagesAdapter(resModel, iRecyclerItemClick)

        holder.binding.rcImages.setOnClickListener {
            iRecyclerItemClick.onItemClick(resModel)
        }

        holder.bind(resModel, iRecyclerItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.restaurant_view,
            parent,
            false
        )
        return ViewHolder(binding as RestaurantViewBinding)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class ViewHolder(
        var binding: RestaurantViewBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: NewDashboardModel.AllRestautants,
            listener: IRecyclerItemClick
        ) {
            binding.restaurantModel = item
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
}