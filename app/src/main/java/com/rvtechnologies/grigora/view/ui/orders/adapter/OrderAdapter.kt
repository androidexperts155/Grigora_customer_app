package com.rvtechnologies.grigora.view.ui.orders.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.OrderItemBinding
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class OrderAdapter(
    var orderList: ArrayList<OrderItemModel>,
    var iRecyclerItemClick: IRecyclerItemClick,
    var currentIndex: Int
) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderModel = orderList[position]
//        orderModel.finalPrice = ""
        orderModel.finalPrice = "₦ " + (orderModel.finalPrice)
        val status = when (orderModel.orderStatus) {
            0 -> holder.itemView.context.getString(R.string.waiting_for_confirmation)
            1 -> holder.itemView.context.getString(R.string.order_schduled)
            2 -> holder.itemView.context.getString(R.string.order_accepted)
            8 -> holder.itemView.context.getString(R.string.being_prepared)
            3 -> holder.itemView.context.getString(R.string.driver_assigned)
            4 -> holder.itemView.context.getString(R.string.picked)
            5 -> holder.itemView.context.getString(R.string.delivered_by) + orderModel.driverName
            6 -> holder.itemView.context.getString(R.string.rejected_by_restaurant)
            7 -> holder.itemView.context.getString(R.string.almostready)
            else -> holder.itemView.context.getString(R.string.waiting_for_confirmation)
        }

//        if (orderModel.orderStatus == 5 || !isCurrent)
//            holder.binding.btnRate.text = holder.itemView.context.getString(R.string.rate_now)
//        else
//            holder.binding.btnRate.text = holder.itemView.context.getString(R.string.track)

        orderModel.status = status


        var isRated = true

        if (orderModel.is_driver_rated == "0")
            isRated = false
        else if (orderModel.is_restaurant_rated == "0")
            isRated = false
        else {
            for (item in orderModel.orderDetails) {
                if (item.is_item_rated == "0") {
                    isRated = false
                    break
                }
            }
        }
        orderModel.is_rated = isRated

        orderModel.idToShow =
            holder.itemView.context.getString(R.string.order_hash).plus(orderModel.id)

        holder.binding.btnDetails.visibility = GONE
        holder.binding.liPast.visibility = GONE

        when (currentIndex) {
            0 -> {
                holder.binding.btnDetails.visibility = VISIBLE
            }
            1 -> {
                holder.binding.liPast.visibility =
                    if (!orderModel.is_rated && (orderModel.orderStatus != 6 && orderModel.orderStatus != 8)) VISIBLE else GONE

            }
            2 -> {

            }
        }


        if (orderModel.orderDetails.isNotEmpty())
            holder.binding.rvOrderItem.adapter =
                OrderMenuItemAdapter(orderModel.orderDetails)
        var nam = ""
        for (item in orderModel.orderDetails) {
            if (nam == "") {
                nam = item.itemName + " x " + item.quantity
            } else
                nam = nam + ", " + item.itemName + " x " + item.quantity
        }

        holder.binding.tvItems.text = nam


        holder.bind(orderModel, iRecyclerItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.order_item, parent, false)
        return ViewHolder(binding as OrderItemBinding)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class ViewHolder(var binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: OrderItemModel,
            iRecyclerItemClick: IRecyclerItemClick
        ) {
            binding.orderModel = item
            binding.executePendingBindings()
            binding.btnDetails.setOnClickListener {
                iRecyclerItemClick.onItemClick(item)
            }
            binding.btnRate.setOnClickListener {
                iRecyclerItemClick.onItemClick(item)
            }
        }
    }


}