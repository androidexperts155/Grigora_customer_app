package com.rvtechnologies.grigora.view.ui.restaurant_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ItemCategoryViewBinding
import com.rvtechnologies.grigora.model.models.ItemCategory
import com.rvtechnologies.grigora.model.models.ItemSubCategory
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.item_category_view.view.*

class ItemCategoryAdapter(
    var itemCategoryList: ArrayList<ItemCategory>,
    var iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<ItemCategoryAdapter.ViewHolder>(), IRecyclerItemClick {
    override fun onItemClick(item: Any) {
        iRecyclerItemClick.onItemClick(item)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryModel = itemCategoryList[position]

        val innerList = categoryModel.itemSubCategory as ArrayList<ItemSubCategory>

        holder.itemView.rvInnerOptions.adapter =
            ItemInnerCategoryAdapter(innerList, categoryModel.selection!!, this)
        holder.bind(categoryModel, iRecyclerItemClick)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.item_category_view,
            parent,
            false
        )
        return ViewHolder(binding as ItemCategoryViewBinding)
    }

    override fun getItemCount(): Int {
        return itemCategoryList.size
    }

    class ViewHolder(var binding: ItemCategoryViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ItemCategory,
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
