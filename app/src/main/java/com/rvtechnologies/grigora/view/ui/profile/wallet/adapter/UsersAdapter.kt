package com.rvtechnologies.grigora.view.ui.profile.wallet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.SearchUserModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.squareup.picasso.Picasso

class UsersAdapter(
    var list: ArrayList<SearchUserModel>,
    var iRecyclerItemClick: IRecyclerItemClick
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var model = list[position]
        holder.tv_name.text = model.name
        holder.tv_email.text = model.email

        val circularProgressDrawable = CircularProgressDrawable(holder.tv_email.context!!)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        if (!model.image.isNullOrEmpty())
            Picasso.get()
                .load(model.image).placeholder(
                    circularProgressDrawable
                )
                .error(
                    circularProgressDrawable
                )
                .into(holder.img_user)

        holder.itemView.setOnClickListener { iRecyclerItemClick.onItemClick(model) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img_user = view.findViewById<ImageView>(R.id.img_user)
        var tv_name = view.findViewById<TextView>(R.id.tv_name)
        var tv_email = view.findViewById<TextView>(R.id.tv_email)
    }

}