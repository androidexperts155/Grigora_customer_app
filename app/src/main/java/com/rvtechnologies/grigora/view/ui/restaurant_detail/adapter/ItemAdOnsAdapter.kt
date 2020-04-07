package com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ItemCategoryViewBinding
import com.rvtechnologies.grigora.databinding.ItemMealAdonsBinding
import com.rvtechnologies.grigora.model.models.ItemCategory
import com.rvtechnologies.grigora.model.models.ItemSubCategory
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import kotlinx.android.synthetic.main.item_category_view.view.*
import kotlinx.android.synthetic.main.item_category_view.view.rvInnerOptions
import kotlinx.android.synthetic.main.item_meal_adons.view.*

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
        var list = ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory>()
        list.addAll(innerList)

        if (categoryModel.max.toInt() > 0) {
            holder.binding.tvChoose.text =
                holder.itemView.context!!.getString(R.string.choose_any) + " " + categoryModel.max
        } else
            holder.binding.tvChoose.visibility = View.GONE

        // show or hide - w optional or required
        var selected = 0
        for (item in categoryModel.item_sub_category) {
            if (item.checked)
                selected++
        }

        if (categoryModel.required == "1") {
//            this field is required
            holder.binding.tvIsRequired.text =
                holder.itemView.context!!.getString(R.string.required_)

            if (selected >= categoryModel.min.toInt() && selected <= categoryModel.max.toInt()) {
//                            this is valid
                holder.binding.tvIsRequired.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context!!,
                        R.color.green
                    )
                )
            } else {
                holder.binding.tvIsRequired.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context!!,
                        R.color.colorPrimaryDark
                    )
                )
            }

        } else {
            holder.binding.tvIsRequired.text =
                holder.itemView.context!!.getString(R.string.optional)
            if (CommonUtils.isDarkMode()) {
                if (selected >= categoryModel.max.toInt()) {
                    holder.binding.tvIsRequired.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context!!,
                            R.color.green
                        )
                    )
                } else
                    holder.binding.tvIsRequired.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context!!,
                            R.color.light_gray
                        )
                    )
            } else {
                if (selected >= categoryModel.max.toInt()) {
                    holder.binding.tvIsRequired.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context!!,
                            R.color.green
                        )
                    )
                } else
                    holder.binding.tvIsRequired.setTextColor(
                        ContextCompat.getColor(
                            holder.itemView.context!!,
                            R.color.text_hint_color
                        )
                    )
            }
        }

        if (holder.itemView.rvInnerOptions.adapter == null)
            holder.itemView.rvInnerOptions.adapter =
                ItemInnerAdOnsAdapter(
                    list,
                    categoryModel.required,
                    categoryModel.min,
                    categoryModel.max,
                    this
                )

        holder.itemView.img_collapse.setOnClickListener {
            if (holder.itemView.rvInnerOptions.visibility == View.VISIBLE) {
                holder.itemView.rvInnerOptions.visibility = View.GONE
                holder.itemView.img_collapse.rotation = -90F
                holder.binding.tvChoose.visibility = View.GONE


            } else {
                holder.itemView.rvInnerOptions.visibility = View.VISIBLE
                holder.itemView.img_collapse.rotation = 0F
                if (categoryModel.max.toInt() == 0)
                    holder.binding.tvChoose.visibility = View.GONE
                else
                    holder.binding.tvChoose.visibility = View.VISIBLE
            }

        }

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
