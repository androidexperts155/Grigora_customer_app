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
        orderModel.finalPriceToShow = ""
        orderModel.finalPriceToShow = "₦ " + (orderModel.finalPrice)


        val status = when (orderModel.orderStatus) {
            0 -> holder.itemView.context.getString(R.string.waiting_for_confirmation)
            1 -> holder.itemView.context.getString(R.string.order_schduled)
            9 -> holder.itemView.context.getString(R.string.preparation_started)
            2 -> holder.itemView.context.getString(R.string.order_accepted)
            8 -> holder.itemView.context.getString(R.string.cancelled_by_you)
            3 -> holder.itemView.context.getString(R.string.driver_assigned)
            4 -> holder.itemView.context.getString(R.string.picked)
            5 -> if (orderModel.driverId == null) holder.itemView.context.getString(R.string.order_delivered) else holder.itemView.context.getString(
                R.string.delivered_by
            ) + " " + orderModel.driverName
            6 -> holder.itemView.context.getString(R.string.rejected_by_restaurant)
            7 -> holder.itemView.context.getString(R.string.almostready)
            else -> holder.itemView.context.getString(R.string.waiting_for_confirmation)
        }


        orderModel.status = status


        var isRated = true

        if (orderModel.is_driver_rated == "0" && orderModel.driverId != null)
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

        if (currentIndex == 2) {
            holder.itemView.setOnClickListener { iRecyclerItemClick.onItemClick(orderModel) }
        }
        orderModel.is_rated = isRated

        if (orderModel.is_already_rated)
            orderModel.is_rated = true

        orderModel.idToShow =
            holder.itemView.context.getString(R.string.order_hash).plus(orderModel.id)

        holder.binding.btnDetails.visibility = GONE
        holder.binding.liPast.visibility = GONE


        //        0=>Waiting for confirmation
//        2=>accepted by restaurant,
//        3=>driver assigned,
//        9=>restaurant starts preparing  get time from notification
//        7=> order is almost ready,   stop prepation time/nill
//        4=>out of delivery,  get driver time
//        5=> deliverd,   stop driver time
//        1=> schedule order ,
//        6=>rejected by restaurant,
//        8=>cancelled by customer,
//        (notification type )11=> show elapsed time and reset counter

        when (currentIndex) {
            0 -> {
                holder.binding.btnDetails.visibility = VISIBLE
            }
            1 -> {
                holder.binding.liPast.visibility = VISIBLE

                holder.binding.btnRate.visibility =
                    if (!orderModel.is_rated && orderModel.orderStatus == 5) VISIBLE else GONE
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
                item.isReorder = false
                iRecyclerItemClick.onItemClick(item)
            }

            binding.btnReorder.setOnClickListener {
                item.isReorder = true
                iRecyclerItemClick.onItemClick(item)
            }
        }
    }
}
