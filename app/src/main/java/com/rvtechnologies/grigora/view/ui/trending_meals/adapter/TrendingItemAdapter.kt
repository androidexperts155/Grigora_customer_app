package com.rvtechnologies.grigora.view.ui.trending_meals.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.MenuItemViewBinding
import com.rvtechnologies.grigora.databinding.TrendingMenuItemViewBinding
import com.rvtechnologies.grigora.model.RestaurantDetailModel
import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.utils.OnItemClickListener
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicks
import kotlinx.android.synthetic.main.menu_item_view.view.*
import kotlinx.android.synthetic.main.menu_item_view.view.bt_add
import kotlinx.android.synthetic.main.menu_item_view.view.img_type
import kotlinx.android.synthetic.main.menu_item_view.view.li_add
import kotlinx.android.synthetic.main.menu_item_view.view.rating
import kotlinx.android.synthetic.main.menu_item_view.view.tv_minus
import kotlinx.android.synthetic.main.menu_item_view.view.tv_plus
import kotlinx.android.synthetic.main.menu_item_view.view.tv_price
import kotlinx.android.synthetic.main.menu_item_view.view.tv_quantity
import kotlinx.android.synthetic.main.trending_menu_item_view.view.*


class TrendingItemAdapter(
    var menuItemList: ArrayList<MenuItemModel>,
    var listener: OnItemClickListener, val quantityClicks: QuantityClicks
) : RecyclerView.Adapter<TrendingItemAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resModel = menuItemList[position]

        if (resModel.item_count_in_cart > 0) {
            holder.itemView.li_add.visibility = View.VISIBLE
            holder.itemView.bt_add.visibility = View.GONE
            holder.itemView.tv_quantity.text = resModel.item_count_in_cart.toString()
        } else {
            holder.itemView.li_add.visibility = View.GONE
            holder.itemView.bt_add.visibility = View.VISIBLE
        }
        holder.itemView.tv_time.text="in "+resModel.time
        holder.itemView.tv_info.text=resModel.total_orders + " Orders("+"${resModel.customers} Customers)"
        holder.itemView.tv_price.text = "₦ " + resModel.price.toString()
        holder.itemView.rating.rating = resModel.avgRatings
        holder.itemView.tv_plus.setOnClickListener {
            quantityClicks.add(0, position)
        }

        holder.itemView.tv_minus.setOnClickListener {
            quantityClicks.minus(0, position)
        }

        holder.itemView.bt_add.setOnClickListener {
            quantityClicks.add(0, position)
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

        holder.bind(resModel, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {


        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.trending_menu_item_view,
            parent,
            false
        )

        return ViewHolder(binding as TrendingMenuItemViewBinding)
    }

    override fun getItemCount(): Int {
        return menuItemList.size
    }

    class ViewHolder(
        var binding: TrendingMenuItemViewBinding
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