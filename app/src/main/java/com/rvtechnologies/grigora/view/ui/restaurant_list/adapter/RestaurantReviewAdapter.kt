package com.rvtechnologies.grigora.view.ui.restaurant_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ItemCategoryViewBinding
import com.rvtechnologies.grigora.databinding.ReviewItemBinding
import com.rvtechnologies.grigora.model.models.ItemCategory
import com.rvtechnologies.grigora.model.models.ItemSubCategory
import com.rvtechnologies.grigora.model.models.ReviewItem
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.item_category_view.view.*


class RestaurantReviewAdapter(var reviewList: ArrayList<ReviewItem>) :
    RecyclerView.Adapter<RestaurantReviewAdapter.ViewHolder>(){


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reviewItem = reviewList[position]
        holder.bind(reviewItem)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.review_item, parent,false)
        return ViewHolder(binding as ReviewItemBinding)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    class ViewHolder(var binding: ReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ReviewItem
        ) {
            binding.reviewItem = item
            binding.executePendingBindings()
        }
    }



}
