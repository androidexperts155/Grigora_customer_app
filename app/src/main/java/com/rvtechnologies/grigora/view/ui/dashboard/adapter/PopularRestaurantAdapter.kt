package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.PopularRestaurantViewBinding
import com.rvtechnologies.grigora.model.models.RestaurantModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.utils.OnItemClickListener


class PopularRestaurantAdapter(var restaurantList: ArrayList<RestaurantModel>, var listener: OnItemClickListener) : RecyclerView.Adapter<PopularRestaurantAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resModel = restaurantList[position]

        resModel.nameToShow=if (GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH) resModel.frenchName else resModel.name
        resModel.addressToShow=if (GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH) resModel.frenchAddress else resModel.address

        holder.bind(resModel,listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.popular_restaurant_view, parent,false)
        return ViewHolder(binding as PopularRestaurantViewBinding)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class ViewHolder(
        var binding: PopularRestaurantViewBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RestaurantModel,
                 listener: OnItemClickListener) {
            binding.restaurantModel = item
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
}