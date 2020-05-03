package com.rvtechnologies.grigora.view.ui.cart.adapter

import android.view.LayoutInflater
import android.view.View
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
import kotlinx.android.synthetic.main.cart_item.view.*

class CartAdapter(
    var cartItemList: ArrayList<CartDetail>,
    var iRecyclerItemClick: IRecyclerItemClick, val quantityClicks: QuantityClicks
) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartModel = cartItemList[position]

        cartModel.itemNameToDisplay = cartModel.quantity + "     " + cartModel.itemName!!
        var price = cartModel.price?.toDouble()!!
        var choicesString = ""
        choicesString = "$choicesString"
        if (!cartModel.item_removeables.isNullOrEmpty())
            for (item in cartModel.item_removeables!!) {
                choicesString =
                    choicesString.plus(" ${holder.itemView.context.getString(R.string.no)}  " + item.name)

                choicesString = "$choicesString,"
            }

        if (!choicesString.isNullOrEmpty())
            choicesString = choicesString.removeSuffix(",")

        if (cartModel.item_choices != null && cartModel.item_choices?.isNotEmpty()!!) {


            for (item in cartModel.item_choices!!) {

                choicesString = if (choicesString.isNullOrEmpty())
                    holder.itemView.context.getString(R.string.add)
                else
                    choicesString + ", " + holder.itemView.context.getString(R.string.add)

                for (innerItem in item.itemSubCategory!!) {
                    choicesString =
                        choicesString.plus(" " + innerItem?.name + ",")
                }
                choicesString = "$choicesString"
            }
            choicesString = choicesString.removeSuffix(",")
            choicesString = "$choicesString"

        }
        if (choicesString == "")
            holder.itemView.textView15.visibility = View.GONE

        cartModel.total = "₦ " + ((price) * cartModel.quantity?.toDouble()!!).toString()

        cartModel.choicesString = choicesString


        holder.itemView.tv_plus.setOnClickListener {
            quantityClicks.add(position, 0)
//            cartModel.quantity = (Integer.parseInt(cartModel.quantity!!) + 1).toString()
            notifyDataSetChanged()
        }

        holder.itemView.tv_minus.setOnClickListener {
            quantityClicks.minus(position, 0)
//            cartModel.quantity = (Integer.parseInt(cartModel.quantity!!) - 1).toString()
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