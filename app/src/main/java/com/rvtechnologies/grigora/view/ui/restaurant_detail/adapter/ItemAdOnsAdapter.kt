package com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ItemCategoryViewBinding
import com.rvtechnologies.grigora.databinding.ItemMealAdonsBinding
import com.rvtechnologies.grigora.model.models.ItemCategory
import com.rvtechnologies.grigora.model.models.ItemSubCategory
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import kotlinx.android.synthetic.main.item_category_view.view.*

class ItemAdOnsAdapter(
    var itemCategoryList: ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory>,
    var iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<ItemAdOnsAdapter.ViewHolder>(), IRecyclerItemClick {
    override fun onItemClick(item: Any) {
        iRecyclerItemClick.onItemClick(item)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryModel = itemCategoryList[position]

        val innerList = categoryModel.item_sub_category
var list=ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory>()
        list.addAll(innerList)
        holder.itemView.rvInnerOptions.adapter =
            ItemInnerAdOnsAdapter(list, categoryModel.selection!!, this)
        holder.bind(categoryModel, iRecyclerItemClick)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.item_meal_adons,
            parent,
            false
        )
        return ViewHolder(binding as ItemMealAdonsBinding)
    }

    override fun getItemCount(): Int {
        return itemCategoryList.size
    }

    class ViewHolder(var binding: ItemMealAdonsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: RestaurantDetailNewModel.MealItem.ItemCategory,
            iRecyclerItemClick: IRecyclerItemClick
        ) {
            binding.itemCategory = item
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                iRecyclerItemClick.onItemClick(item)
            }
        }
    }


}
