package com.rvtechnologies.grigora.view.ui.restaurant_list.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
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
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.TimeAgo
import kotlinx.android.synthetic.main.item_category_view.view.*
import kotlinx.android.synthetic.main.review_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RestaurantReviewAdapter(var reviewList: ArrayList<ReviewItem>) :
    RecyclerView.Adapter<RestaurantReviewAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reviewItem = reviewList[position]

        var utcDate = CommonUtils.getUtcDate(
            holder.itemView.context!!,
            (reviewList[position]).createdAt!!,
            "yyyy-MM-dd HH:mm:ss"
        )


        var format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                holder.itemView.context!!.resources.configuration.locales[0]
            )
        } else
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                holder.itemView.context!!.resources.configuration.locale
            )

        format.timeZone = TimeZone.getDefault()

        holder.itemView.tv_time.text =
            TimeAgo.getTimeAgo(format.parse(format.format(utcDate)).time)

        if (reviewItem.review.isNullOrEmpty()) {
            holder.itemView.textView47.visibility = View.GONE
        }







        holder.bind(reviewItem)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.review_item, parent, false)
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
