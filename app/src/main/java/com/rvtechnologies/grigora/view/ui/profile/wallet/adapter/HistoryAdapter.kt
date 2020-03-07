package com.rvtechnologies.grigora.view.ui.profile.wallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.WalletHistoryItemBinding
import com.rvtechnologies.grigora.model.WalletHistoryModel
import com.rvtechnologies.grigora.utils.CommonUtils
import kotlinx.android.synthetic.main.wallet_history_item.view.*

class HistoryAdapter(
    var historyList: ArrayList<WalletHistoryModel.History>
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val historyModel = historyList[position]
//        3=>add in grigora wallet,4=>deduct from grigora wallet,5:send money,6:receive money

        holder.itemView.tv_time.text = CommonUtils.getFormattedTimeOrDate(
            CommonUtils.getUtcDate(
                holder.binding.root.context,
                historyModel.createdAt,
                "yyyy-MM-dd HH:mm:ss"
            ), "yyyy-MM-dd HH:mm", "dd MMM,yyyy HH:mm"
        )

        when (historyModel.type) {
            "3" -> {
                holder.itemView.tv_amount.text = "+ ₦ ${historyModel.amount}"

                holder.itemView.tv_amount.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.green
                    )
                )

                holder.itemView.tv_username.text =
                    holder.itemView.context!!.getString(R.string.money_added)


                holder.itemView.tv_wallet.visibility = View.GONE
            }

            "4" -> {
                holder.itemView.tv_amount.text = "- ₦ ${historyModel.amount}"

                holder.itemView.tv_amount.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorPrimaryDark
                    )
                )

                holder.itemView.tv_username.text =
                    holder.itemView.context!!.getString(R.string.money_deducted)

                holder.itemView.tv_wallet.visibility = View.GONE


            }

            "5" -> {
                holder.itemView.tv_amount.text = "- ₦ ${historyModel.amount}"
                holder.itemView.tv_amount.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.colorPrimaryDark
                    )
                )

                holder.itemView.tv_username.text = historyList[position].otherUserName
                holder.itemView.tv_wallet.text = historyList[position].other_user_wallet_id
            }

            "6" -> {
                holder.itemView.tv_amount.text = "+ ₦ ${historyModel.amount}"
                holder.itemView.tv_amount.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.green
                    )
                )

                holder.itemView.tv_username.text = historyList[position].otherUserName
                holder.itemView.tv_wallet.text = historyList[position].other_user_wallet_id
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
            item: WalletHistoryModel.History
        ) {
            binding.historyModel = item
            binding.executePendingBindings()

        }
    }


}