package com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ItemInnerAdonBinding
import com.rvtechnologies.grigora.model.models.ItemSubCategory
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import kotlinx.android.synthetic.main.item_inner_adon.view.*


class ItemInnerAdOnsAdapter(
    var itemInnerCategoryList: ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory>,
    var selection: String,
    var iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<ItemInnerAdOnsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val innerCategoryModel = itemInnerCategoryList[position]
        innerCategoryModel.addOnPriceString =
            "(+" + holder.itemView.context.getString(R.string.currency_sym) + innerCategoryModel.add_on_price + ")"

        holder.binding.chkIsChecked.setOnClickListener {
            if (selection == "1") {
                val newList =
                    ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory>()
                for (innerItem in itemInnerCategoryList) {
                    if (innerItem.id == innerCategoryModel.id) {
                        innerItem.checked = holder.itemView.chkIsChecked.isChecked
                        innerCategoryModel.checked = holder.itemView.chkIsChecked.isChecked
                        iRecyclerItemClick.onItemClick(innerCategoryModel)
                    } else {

                        innerItem.checked = false
                        iRecyclerItemClick.onItemClick(innerItem)
                    }
                    newList.add(innerItem)
                }
                updateList(newList)
            } else {
                innerCategoryModel.checked = holder.itemView.chkIsChecked.isChecked
                iRecyclerItemClick.onItemClick(innerCategoryModel)
                notifyDataSetChanged()
            }
        }
        holder.bind(innerCategoryModel, iRecyclerItemClick)
    }

    private fun updateList(newList: ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory>) {
        itemInnerCategoryList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.item_inner_adon,
            parent,
            false
        )
        return ViewHolder(binding as ItemInnerAdonBinding)
    }

    override fun getItemCount(): Int {
        return itemInnerCategoryList.size
    }

    class ViewHolder(var binding: ItemInnerAdonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory,
            iRecyclerItemClick: IRecyclerItemClick
        ) {
            binding.itemSubCategory = item

            binding.executePendingBindings()
            binding.root.setOnClickListener {
                iRecyclerItemClick.onItemClick(item)
            }
        }
    }
}
