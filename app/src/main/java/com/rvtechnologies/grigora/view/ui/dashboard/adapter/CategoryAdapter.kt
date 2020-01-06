package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.CategoryItemBinding
import com.rvtechnologies.grigora.model.models.CategoryModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.category_item.view.*


class CategoryAdapter(
    var categoryList: ArrayList<CategoryModel>,
    var iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryModel = categoryList[position]
        if (categoryModel.checked) {
            holder.itemView.tv_name.setTextColor(Color.WHITE)

            holder.itemView.card_main.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorPrimaryDark
                )
            )

            holder.itemView.tv_name.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )


            holder.itemView.img_category.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        } else {
            holder.itemView.tv_name.setTextColor(Color.DKGRAY)


            holder.itemView.tv_name.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.textBlack
                )
            )

            holder.itemView.img_category.setColorFilter(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.textBlack
                )
            )

            holder.itemView.card_main.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.grey_bg
                )
            )
        }

        holder.bind(categoryModel, iRecyclerItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.category_item,
            parent,
            false
        )


        return ViewHolder(binding as CategoryItemBinding)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class ViewHolder(var binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CategoryModel,
            iRecyclerItemClick: IRecyclerItemClick
        ) {
            binding.categoryModel = item

            binding.executePendingBindings()
            binding.root.setOnClickListener {
                iRecyclerItemClick.onItemClick(item)
            }
        }
    }


}