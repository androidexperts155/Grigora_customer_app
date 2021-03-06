package com.rvtechnologies.grigora.view.ui.cart.adapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ItemAlsoOrderedBinding
import com.rvtechnologies.grigora.databinding.MenuItemViewBinding
import com.rvtechnologies.grigora.model.RestaurantDetailModel
 import com.rvtechnologies.grigora.model.models.CartDataModel
import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.utils.OnItemClickListener
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicks
import kotlinx.android.synthetic.main.item_also_ordered.view.*


class AlsoOrderedCartAdapter(
    var menuItemList: ArrayList<RestaurantDetailNewModel.MealItem>,
    var listener: OnItemClickListener, val quantityClicks: QuantityClicks, val pos: Int
) : RecyclerView.Adapter<AlsoOrderedCartAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resModel = menuItemList[position]
//        holder.itemView.li_add.visibility = View.GONE
        holder.itemView.bt_add.visibility = View.VISIBLE

        holder.itemView.tv_price.text = "₦ " + resModel.price
        holder.itemView.rating.rating = resModel.avg_ratings.toFloat()
//        holder.itemView.tv_plus.setOnClickListener {
//            quantityClicks.add(pos, position)
//        }
//
//        holder.itemView.tv_minus.setOnClickListener {
//            quantityClicks.minus(pos, position)
//        }

        holder.itemView.bt_add.setOnClickListener {
            quantityClicks.add(pos, position)
        }

        when (resModel.pure_veg) {
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
            R.layout.item_also_ordered,
            parent,
            false
        )

        return ViewHolder(binding as ItemAlsoOrderedBinding)
    }

    override fun getItemCount(): Int {
        return menuItemList.size
    }

    class ViewHolder(
        var binding: ItemAlsoOrderedBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: RestaurantDetailNewModel.MealItem,
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