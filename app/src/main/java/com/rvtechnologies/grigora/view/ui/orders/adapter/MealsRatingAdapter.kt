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
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class MealsRatingAdapter(
    var orderList: ArrayList<OrderItemModel>,
    var iRecyclerItemClick: IRecyclerItemClick,
    var isCurrent: Boolean
) :
    RecyclerView.Adapter<MealsRatingAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderModel = orderList[position]
        orderModel.finalPrice = "Total : " + "$".plus(orderModel.finalPrice)
        val status = when (orderModel.orderStatus) {
            0 -> "Waiting for confirmation"
            1 -> "Order scheduled"
            2 -> "Order Accepted"
            8 -> "Order is being prepared"
            3 -> "Driver assigned"
            4 -> "Order picked up by driver,order is now its way to you"
            5 -> "Order completed Delivered by " + orderModel.driverName
            6 -> "Rejected by Restaurant"
            7 -> "Order almost ready"
            else -> "Waiting"
        }

//        if (orderModel.orderStatus == 5 || !isCurrent)
//            holder.binding.btnRate.text = holder.itemView.context.getString(R.string.rate_now)
//        else
//            holder.binding.btnRate.text = holder.itemView.context.getString(R.string.track)

        orderModel.orderStatus = status.toInt()
        orderModel.idToShow = "ORDER#".plus(orderModel.id)

        holder.binding.btnDetails.visibility = if (isCurrent) VISIBLE else GONE

        holder.binding.btnRate.visibility =
            if (!isCurrent && orderModel.is_rated && orderModel.orderStatus != 6) VISIBLE else GONE


        if (orderModel.orderDetails.isNotEmpty())
            holder.binding.rvOrderItem.adapter =
                OrderMenuItemAdapter(orderModel.orderDetails)
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