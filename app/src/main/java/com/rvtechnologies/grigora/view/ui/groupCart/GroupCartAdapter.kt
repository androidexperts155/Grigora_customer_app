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
                    if (choicesString.isNullOrEmpty())
                        choicesString =
                            holder.itemView.context.getString(R.string.add)
                    else
                        choicesString =
                            choicesString + ", " + holder.itemView.context.getString(R.string.add)


                    for (innerItem in item.itemSubCategory!!) {
                        price += innerItem?.addOnPrice!!

                        if (innerItem.item_sub_sub_category!!.isNotEmpty()) {
                            for (i in innerItem.item_sub_sub_category!!) {
                                if (i?.add_on_price!! > 0)
                                    price += i?.add_on_price?.toDouble()!!
                            }
                        }
                        choicesString =
                            choicesString.plus(" " + innerItem.name + ",")
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
                cartModel.quantity = (Integer.parseInt(cartModel.quantity!!) + 1).toString()
                notifyDataSetChanged()
            }

            holder.itemView.tv_minus.setOnClickListener {
                quantityClicks.minus(position, 0)
                cartModel.quantity = (Integer.parseInt(cartModel.quantity!!) - 1).toString()
                notifyDataSetChanged()
            }

            if (cartModel.user_id.equals(
                    CommonUtils.getPrefValue(
                        holder.itemView.context!!,
                        PrefConstants.ID
                    )
                )
            )
                holder.itemView.linearLayout.visibility = View.VISIBLE
            else
                holder.itemView.linearLayout.visibility = View.INVISIBLE


            holder.bind(cartModel, iRecyclerItemClick)
        } else {
            holder as MyView
            if ((cartItemList[position] as com.rvtechnologies.grigora.model.CartDetail).id.toString() == CommonUtils.getPrefValue(
                    holder.itemView.context,
                    PrefConstants.ID
                )
            )
                holder.tv_title.text =
                    (cartItemList[position] as com.rvtechnologies.grigora.model.CartDetail).name + holder.itemView.context.getString(
                        R.string.your_order
                    )
            else
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