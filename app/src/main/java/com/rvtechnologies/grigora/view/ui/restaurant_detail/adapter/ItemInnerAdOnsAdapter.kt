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
import com.rvtechnologies.grigora.model.models.ItemSubCategory
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import kotlinx.android.synthetic.main.item_inner_adon.view.*


class ItemInnerAdOnsAdapter(
    var itemInnerCategoryList: ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory>,
    var required: String,
    var min: String,
    var max: String,
    var iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<ItemInnerAdOnsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val innerCategoryModel = itemInnerCategoryList[position]
        innerCategoryModel.addOnPriceString =
            "(+" + holder.itemView.context.getString(R.string.currency_sym) + innerCategoryModel.add_on_price + ")"

        holder.itemView.li_main.setOnClickListener {
            when {
                max == "1" -> {
                    //                only single item can be selected
                    val newList =
                        ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory>()
                    for (innerItem in itemInnerCategoryList) {
                        if (innerItem.id == innerCategoryModel.id) {
                            if (holder.itemView.li_main.tag == "false") {
                                //                            select now
                                holder.itemView.li_main.tag = "true"
                                innerItem.checked = true
                                innerCategoryModel.checked = true
                            } else {
                                //                              deselect now
                                holder.itemView.li_main.tag = "false"
                                innerItem.checked = false
                                innerCategoryModel.checked = false
                            }

                            iRecyclerItemClick.onItemClick(innerCategoryModel)
                        } else {
                            holder.itemView.li_main.tag = "false"
                            innerItem.checked = false
                            iRecyclerItemClick.onItemClick(innerItem)
                        }
                        newList.add(innerItem)
                    }



                    if (holder.itemView.li_main.tag == "true") {
                        holder.itemView.img_selected.visibility = View.VISIBLE
                        holder.itemView.img_desel.visibility = View.GONE
                        holder.itemView.tv_name.setTextColor(
                            ContextCompat.getColor(
                                holder.itemView.context!!,
                                R.color.colorPrimaryDark
                            )
                        )
                        holder.itemView.tv_price.setTextColor(
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
                            holder.itemView.tv_price.setTextColor(
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
                            holder.itemView.tv_price.setTextColor(
                                ContextCompat.getColor(
                                    holder.itemView.context!!,
                                    R.color.textBlack
                                )
                            )
                        }
                    }
                    updateList(newList)
                }
                valid(holder) -> {
                    if (holder.itemView.li_main.tag == "false") {
                        holder.itemView.li_main.tag = "true"
                        innerCategoryModel.checked = true
                    } else {
                        holder.itemView.li_main.tag = "false"
                        innerCategoryModel.checked = false

                    }

                    if (holder.itemView.li_main.tag == "true") {
                        holder.itemView.img_selected.visibility = View.VISIBLE
                        holder.itemView.img_desel.visibility = View.GONE
                        holder.itemView.tv_name.setTextColor(
                            ContextCompat.getColor(
                                holder.itemView.context!!,
                                R.color.colorPrimaryDark
                            )
                        )
                        holder.itemView.tv_price.setTextColor(
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
                            holder.itemView.tv_price.setTextColor(
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
                            holder.itemView.tv_price.setTextColor(
                                ContextCompat.getColor(
                                    holder.itemView.context!!,
                                    R.color.textBlack
                                )
                            )
                        }
                    }
                    iRecyclerItemClick.onItemClick(innerCategoryModel)
                    notifyDataSetChanged()
                }
                else -> {
                    YoYo.with(Techniques.Bounce)
                        .duration(200)
                        .playOn(holder.itemView)
                }
            }
        }
        holder.bind(innerCategoryModel, iRecyclerItemClick)
    }

    private fun valid(holder: ViewHolder): Boolean {
        if (required == "0" && max.toInt() == 0)
            return true

        if (holder.itemView.li_main.tag == "true")
            return true
        var count = 0
        for (item in itemInnerCategoryList) {
            if (item.checked)
                count++
        }
        return count != max.toInt()
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
//            binding.root.setOnClickListener {
//                iRecyclerItemClick.onItemClick(item)
//            }
        }
    }
}
