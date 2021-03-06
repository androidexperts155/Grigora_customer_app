package com.rvtechnologies.grigora.view.ui.restaurant_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.MenuItemViewBinding
import com.rvtechnologies.grigora.model.RestaurantDetailModel
import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.utils.OnItemClickListener
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicks
import kotlinx.android.synthetic.main.menu_item_view.view.*


class RestaurantItemAdapter(
    var menuItemList: ArrayList<MenuItemModel>,
    var listener: OnItemClickListener, val quantityClicks: QuantityClicks, val pos: Int
) : RecyclerView.Adapter<RestaurantItemAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resModel = menuItemList[position]
        resModel.index = pos

        if (resModel.item_count_in_cart > 0) {
            holder.itemView.li_add.visibility = View.VISIBLE
            holder.itemView.bt_add.visibility = View.GONE
            holder.itemView.tv_quantity.text = resModel.item_count_in_cart.toString()
        } else {
            holder.itemView.li_add.visibility = View.GONE
            holder.itemView.bt_add.visibility = View.VISIBLE
        }

        when (resModel.pureVeg) {
            "1" -> {
                holder.itemView.img_type.setImageResource(R.drawable.veg)
            }
            "2" -> {
                holder.itemView.img_type.setImageResource(R.drawable.containes_egg)
            }
            "0" -> {
                holder.itemView.img_type.setImageResource(R.drawable.non_veg)
            }
        }


        holder.itemView.tv_price.text="₦ "+resModel.price.toString()
        holder.itemView.rating.rating=resModel.avgRatings.toFloat()
        holder.itemView.tv_plus.setOnClickListener {
            quantityClicks.add(pos, position)
        }

        holder.itemView.tv_minus.setOnClickListener {
            quantityClicks.minus(pos, position)
        }

        holder.itemView.bt_add.setOnClickListener {
            quantityClicks.add(pos, position)
        }

        holder.bind(resModel, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {

        var layout = 0
        if (pos == -1) {
            layout = R.layout.item_popular_dish
        } else {
            layout = R.layout.menu_item_view
        }
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            layout,
            parent,
            false
        )

        return ViewHolder(binding as MenuItemViewBinding)
    }

    override fun getItemCount(): Int {
        return menuItemList.size
    }

    class ViewHolder(
        var binding: MenuItemViewBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: MenuItemModel,
            listener: OnItemClickListener
        ) {
            binding.itemDetail = item
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
}