package com.rvtechnologies.grigora.view.ui.restaurant_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.CartItemBinding
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicks
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicksDialog
import kotlinx.android.synthetic.main.cart_item.view.*

class ItemsCartAdapter(
    var cartItemList: ArrayList<CartDetail>,
    var iRecyclerItemClick: IRecyclerItemClick, val quantityClicks: QuantityClicksDialog
) :
    RecyclerView.Adapter<ItemsCartAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartModel = cartItemList[position]
        val isFrench = GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH

        cartModel.itemNameToDisplay =
            if (isFrench) cartModel.itemFrenchName.toString() else cartModel.itemName.toString()
        var price = cartModel.price?.toDouble()!!

        var choicesString = ""
        if (cartModel.item_choices != null && cartModel.item_choices?.isNotEmpty()!!) {

            choicesString = "$choicesString("
            for (item in cartModel.item_choices!!) {

                choicesString =
                    choicesString.plus(" " + if (isFrench) item.frenchName else item.name + " : ")
                for (innerItem in item.itemSubCategory!!) {
                    price += innerItem?.addOnPrice!!
                    choicesString =
                        choicesString.plus(" " + if (isFrench) innerItem.frenchName else innerItem.name)
                }
                choicesString = "$choicesString,"
            }
            choicesString = choicesString.removeSuffix(",")
            choicesString = "$choicesString)"
        }

        cartModel.total = ((price) * cartModel.quantity?.toDouble()!!).toString()

        cartModel.choicesString = choicesString
        holder.itemView.tv_minus.setOnClickListener {
            quantityClicks.dialogMinus(position)
            cartModel.quantity = (Integer.parseInt(cartModel.quantity!!) - 1).toString()
            notifyDataSetChanged()
        }

        holder.itemView.tv_plus.setOnClickListener {
            quantityClicks.dialogAdd(position)
            cartModel.quantity = (Integer.parseInt(cartModel.quantity!!) + 1).toString()
            notifyDataSetChanged()
        }

        holder.bind(cartModel, iRecyclerItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.cart_item, parent, false)
        return ViewHolder(binding as CartItemBinding)
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    class ViewHolder(var binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CartDetail,
            iRecyclerItemClick: IRecyclerItemClick
        ) {
            binding.cartItem = item
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                iRecyclerItemClick.onItemClick(item)
            }
        }
    }

}