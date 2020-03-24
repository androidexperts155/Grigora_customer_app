package com.rvtechnologies.grigora.view.ui.offers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.OfferItemBinding
import com.rvtechnologies.grigora.model.models.OfferModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.offer_item.view.*


class OffersAdapter(
    private var offerList: ArrayList<OfferModel>,
    var iRecyclerItemClick: IRecyclerItemClick
) : RecyclerView.Adapter<OffersAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offerModel = offerList[position]



        if (offerModel.is_valid!!) {
            holder.itemView.bt_apply.setOnClickListener {
                iRecyclerItemClick.onItemClick(offerModel)
            }
            holder.itemView.bt_apply.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        } else {
            holder.itemView.bt_apply.setOnClickListener(null)
            holder.itemView.bt_apply.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.viewbg
                )
            )
        }

        holder.bind(offerModel, iRecyclerItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.offer_item, parent, false)
        return ViewHolder(binding as OfferItemBinding)
    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    class ViewHolder(var binding: OfferItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: OfferModel,
            iRecyclerItemClick: IRecyclerItemClick
        ) {
            binding.offerModel = item
            binding.executePendingBindings()

        }
    }

}