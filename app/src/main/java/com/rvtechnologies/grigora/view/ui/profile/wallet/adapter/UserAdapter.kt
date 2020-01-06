package com.rvtechnologies.grigora.view.ui.profile.wallet.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.UserToSendItemBinding
import com.rvtechnologies.grigora.model.SearchUserModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick

class UserAdapter(
    var usersList: ArrayList<SearchUserModel >,
    var iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userModel = usersList[position]
        holder.bind(userModel, iRecyclerItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.user_to_send_item, parent, false)
        return ViewHolder(binding as UserToSendItemBinding)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    class ViewHolder(var binding: UserToSendItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: SearchUserModel ,
            iRecyclerItemClick: IRecyclerItemClick
        ) {
            binding.userModel = item
            binding.executePendingBindings()
            binding.mainLayout.setOnClickListener {
                iRecyclerItemClick.onItemClick(item)
            }

        }
    }


}