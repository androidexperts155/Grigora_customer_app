package com.rvtechnologies.grigora.view.ui.profile.wallet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.WalletHistoryItemBinding
import com.rvtechnologies.grigora.model.WalletHistoryModel
import kotlinx.android.synthetic.main.wallet_history_item.view.*

class HistoryAdapter(
    var historyList: ArrayList<WalletHistoryModel.Data>
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyModel = historyList[position]

//        3=>add in grigora wallet,4=>deduct from grigora wallet,5:send money,6:receive money

        when (historyModel.type) {
            "3" -> {
                holder.itemView.tv_amount.text = "+ ₦ ${historyModel.amount}"
                holder.itemView.tv_amount.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.green
                    )
                )

                holder.itemView.tv_username.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.quantum_grey
                    )
                )

                holder.itemView.tv_email.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.quantum_grey
                    )
                )
            }

            "4" -> {
                holder.itemView.tv_amount.text = "- ₦ ${historyModel.amount}"
                holder.itemView.tv_amount.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorPrimaryDark
                    )
                )

                holder.itemView.tv_username.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )

                holder.itemView.tv_email.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
            }

            "5" -> {
                holder.itemView.tv_amount.text = "- ₦ ${historyModel.amount}"
                holder.itemView.tv_amount.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorPrimaryDark
                    )
                )

                holder.itemView.tv_username.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )

                holder.itemView.tv_email.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
            }

            "6" -> {
                holder.itemView.tv_amount.text = "+ ₦ ${historyModel.amount}"
                holder.itemView.tv_amount.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.green
                    )
                )

                holder.itemView.tv_username.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.quantum_grey
                    )
                )

                holder.itemView.tv_email.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.quantum_grey
                    )
                )
            }

        }



        holder.bind(historyModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                inflater,
                R.layout.wallet_history_item,
                parent,
                false
            )
        return ViewHolder(binding as WalletHistoryItemBinding)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    class ViewHolder(var binding: WalletHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: WalletHistoryModel.Data
        ) {
            binding.historyModel = item
            binding.executePendingBindings()

        }
    }


}