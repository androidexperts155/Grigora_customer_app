package com.rvtechnologies.grigora.view.ui.groupCart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.CartItemBinding
import com.rvtechnologies.grigora.model.Cart
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicks
import kotlinx.android.synthetic.main.cart_item.view.*

class GroupCartAdapter(
    val creatorId: String,
    var cartItemList: ArrayList<GroupCartType>,
    var iRecyclerItemClick: IRecyclerItemClick, val quantityClicks: QuantityClicks
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TITLE = 1
    val CART = 2


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is CartViewHolder) {

            val cartModel = cartItemList[position] as CartDetail
            val isFrench = GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH

            cartModel.itemNameToDisplay = cartModel.quantity!! + "     " + cartModel.itemName!!

            var price = cartModel.price?.toDouble()!!

            var choicesString = ""
            if (cartModel.item_choices != null && cartModel.item_choices?.isNotEmpty()!!) {

                choicesString = "$choicesString"
                for (item in cartModel.item_choices!!) {

//                choicesString =
//                    choicesString.plus(" " + if (isFrench) item.frenchName else item.name + " : ")
                    for (innerItem in item.itemSubCategory!!) {
                        price += innerItem?.addOnPrice!!
                        choicesString =
                            choicesString.plus(" " + if (isFrench) innerItem.frenchName else innerItem.name)
                    }
                    choicesString = "$choicesString,"
                }
                choicesString = choicesString.removeSuffix(",")
                choicesString = "$choicesString"

            }
            if (choicesString == "")
                holder.itemView.textView15.visibility = View.GONE

            cartModel.total = ((price) * cartModel.quantity?.toDouble()!!).toString()

            cartModel.choicesString = choicesString


            holder.itemView.tv_plus.setOnClickListener {
                quantityClicks.add(position, 0)
                cartModel.quantity = (Integer.parseInt(cartModel.quantity!!) + 1).toString()
                notifyDataSetChanged()
            }

            holder.itemView.tv_minus.setOnClickListener {
                quantityClicks.minus(position, 0)
                cartModel.quantity = (Integer.parseInt(cartModel.quantity!!) - 1).toString()
                notifyDataSetChanged()
            }

        /*    if (cartModel.user_id.equals(
                    CommonUtils.getPrefValue(
                        holder.itemView.context!!,
                        PrefConstants.ID
                    )
                )
            )
                holder.itemView.linearLayout.visibility = View.VISIBLE
            else
                holder.itemView.linearLayout.visibility = View.GONE*/


            holder.bind(cartModel, iRecyclerItemClick)
        } else {
            holder as MyView
            holder.tv_title.text =
                (cartItemList[position] as com.rvtechnologies.grigora.model.CartDetail).name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == CART) {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ViewDataBinding>(
                inflater,
                R.layout.cart_item,
                parent,
                false
            )
            return CartViewHolder(binding as CartItemBinding)
        } else {
            return MyView(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_title,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (cartItemList[position] is CartDetail) {
            CART
        } else {
            TITLE
        }
    }

    class CartViewHolder(var binding: CartItemBinding) :
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

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var tv_title: TextView = view.findViewById(R.id.tv_title)
    }
}