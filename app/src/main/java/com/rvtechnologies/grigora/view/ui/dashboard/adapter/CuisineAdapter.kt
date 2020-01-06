
package com.rvtechnologies.grigora.view.ui.dashboard.adapter
/*
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.CuisineModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.category_item.view.*


class CuisineAdapter(var cuisineList: ArrayList<CuisineModel>,var iRecyclerItemClick: IRecyclerItemClick) :
    RecyclerView.Adapter<CuisineAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cuisineModel = cuisineList[position]
        if(cuisineModel.checked)
        {
            holder.itemView.txtCuisineName.setTextColor(Color.BLACK)
            holder.itemView.txtCuisineName.setTypeface(null, Typeface.BOLD)
            holder.itemView.txtCuisineName.scaleX=1.1f
            holder.itemView.txtCuisineName.scaleY=1.1f
        }
        else{
            holder.itemView.txtCuisineName.setTextColor(Color.DKGRAY)
            holder.itemView.txtCuisineName.setTypeface(null, Typeface.NORMAL)
            holder.itemView.txtCuisineName.scaleX=1f
            holder.itemView.txtCuisineName.scaleY=1f
        }

        holder.bind(cuisineModel,iRecyclerItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.category_item, parent,false)


        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cuisineList.size
    }

    class ViewHolder(var binding: DataBindingUtil) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CuisineModel,
            iRecyclerItemClick: IRecyclerItemClick
        ) {
            binding.cuisineModel = item

            binding.executePendingBindings()
            binding.root.setOnClickListener {
                iRecyclerItemClick.onItemClick(item)
            }
        }
    }



}*/
