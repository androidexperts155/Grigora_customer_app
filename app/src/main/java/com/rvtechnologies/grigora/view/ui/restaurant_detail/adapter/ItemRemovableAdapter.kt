package com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ItemInnerAdonBinding
import com.rvtechnologies.grigora.databinding.ItemRemovableBinding
import com.rvtechnologies.grigora.model.models.ItemSubCategory
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.ShowSubAdOnModel
import kotlinx.android.synthetic.main.item_inner_adon.view.*


class ItemRemovableAdapter(
    var itemInnerCategoryList: ArrayList<RestaurantDetailNewModel.MealItem.Removables>,
    var iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<ItemRemovableAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val innerCategoryModel = itemInnerCategoryList[position]
        if (innerCategoryModel.checked) {
            holder.itemView.img_selected.visibility = View.VISIBLE
            holder.itemView.img_desel.visibility = View.GONE
            holder.itemView.tv_name.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context!!,
                    R.color.colorPrimaryDark
                )
            )
        } else {
            holder.itemView.img_selected.visibility = View.GONE
            holder.itemView.img_desel.visibility = View.VISIBLE
            if (CommonUtils.isDarkMode()) {
                holder.itemView.tv_name.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context!!,
                        R.color.white
                    )
                )
            } else {
                holder.itemView.tv_name.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context!!,
                        R.color.textBlack
                    )
                )
            }
        }

        holder.itemView.li_main.setOnClickListener {
            iRecyclerItemClick.onItemClick(position)

        }
        holder.bind(innerCategoryModel, iRecyclerItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.item_removable,
            parent,
            false
        )
        return ViewHolder(binding as ItemRemovableBinding)
    }

    override fun getItemCount(): Int {
        return itemInnerCategoryList.size
    }

    class ViewHolder(var binding: ItemRemovableBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: RestaurantDetailNewModel.MealItem.Removables,
            iRecyclerItemClick: IRecyclerItemClick
        ) {
            binding.itemSubCategory = item

            binding.executePendingBindings()
//            binding.root.setOnClickListener {
//                iRecyclerItemClick.onItemClick(item)
//            }
        }
    }
}
