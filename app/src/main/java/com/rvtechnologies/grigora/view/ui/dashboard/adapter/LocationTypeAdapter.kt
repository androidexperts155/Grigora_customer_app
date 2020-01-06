package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.LocationTypeItemBinding
import com.rvtechnologies.grigora.model.models.LocationTypeModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick


class LocationTypeAdapter(var locationTypeList: ArrayList<LocationTypeModel>, var iRecyclerItemClick: IRecyclerItemClick) :
    RecyclerView.Adapter<LocationTypeAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locationModel = locationTypeList[position]
        holder.bind(locationModel,iRecyclerItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.location_type_item, parent,false)


        return ViewHolder(binding as LocationTypeItemBinding)
    }

    override fun getItemCount(): Int {
        return locationTypeList.size
    }

    class ViewHolder(var binding: LocationTypeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: LocationTypeModel,
            iRecyclerItemClick: IRecyclerItemClick
        ) {
            binding.locationTypeModel = item

            binding.executePendingBindings()
            binding.root.setOnClickListener {
                iRecyclerItemClick.onItemClick(item)
            }
        }
    }



}