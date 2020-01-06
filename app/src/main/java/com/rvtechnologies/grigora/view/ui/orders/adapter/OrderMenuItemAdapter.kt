package com.rvtechnologies.grigora.view.ui.orders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.OrderMenuItemBinding
 import com.rvtechnologies.grigora.model.models.OrderItemModel

class OrderMenuItemAdapter(var orderItemList: ArrayList<OrderItemModel.OrderDetail>) :
    RecyclerView.Adapter<OrderMenuItemAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItemModel = orderItemList[position]
        orderItemModel.orderItem = orderItemModel.itemName + " x " + orderItemModel.quantity
        holder.bind(orderItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.order_menu_item,
            parent,
            false
        )
        return ViewHolder(binding as OrderMenuItemBinding)
    }

    override fun getItemCount(): Int {
        return orderItemList.size
    }

    class ViewHolder(var binding: OrderMenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: OrderItemModel.OrderDetail
        ) {
            binding.orderDetail = item
            binding.executePendingBindings()
        }
    }


}

