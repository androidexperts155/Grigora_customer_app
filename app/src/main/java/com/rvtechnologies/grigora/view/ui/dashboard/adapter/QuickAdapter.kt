package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.MenuItemCardViewBinding
import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.utils.*
import kotlinx.android.synthetic.main.menu_item_card_view.view.*


class QuickAdapter(var itemsList: ArrayList<MenuItemModel>, var listener: OnItemClickListener) :
    RecyclerView.Adapter<QuickAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resModel = itemsList[position]

        resModel.nameToShow =
            if (GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH) resModel.frenchName!! else resModel.name!!
//        resModel.addressToShow=if (GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH) resModel.frenchAddress else resModel.address

        val token = CommonUtils.getPrefValue(holder.itemView.context, PrefConstants.TOKEN)

        if (token.isBlank())
            holder.itemView.tglLike.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_like_default)
        else
            holder.itemView.tglLike.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.like_button)

        holder.itemView.tglLike.setOnClickListener {
            if (token.isBlank()) {
                listener.onItemClick(0)
            } else {
                CommonUtils.doBounceAnimation(holder.itemView.tglLike)

                var check = 1
                if (!holder.itemView.tglLike.isChecked)
                    check = 0

                var map = HashMap<String, Int>()
                map.put("check", check)
                map.put("itemId", resModel.id!!)
                listener.onItemClick(map)
            }
        }



        holder.bind(resModel, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.menu_item_card_view,
            parent,
            false
        )



        return ViewHolder(binding as MenuItemCardViewBinding)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    class ViewHolder(
        var binding: MenuItemCardViewBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: MenuItemModel,
            listener: OnItemClickListener
        ) {
            binding.menuItemModel = item
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
}