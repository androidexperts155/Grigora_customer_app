package com.rvtechnologies.grigora.view.ui.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ItemRestSearchBinding
import com.rvtechnologies.grigora.databinding.RestaurantViewBinding
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.ImagesAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.quiz_fragment.*


class SearchRestaurantAdapter(
    var restaurantList: ArrayList<NewDashboardModel.AllRestautants>,
    val iRecyclerItemClick: IRecyclerItemClick
) : RecyclerView.Adapter<SearchRestaurantAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resModel = restaurantList[position]

//        resModel.nameToShow =
//            if (GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH) resModel.frenchName else resModel.name
//        resModel.addressToShow =
//            if (GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH) resModel.frenchAddress else resModel.address
        resModel.reviewToShow =
            "(${resModel.reviews} ${holder.itemView.context!!.getString(R.string.reviews)})"

        resModel.distance = "${CommonUtils.calculateDistance(
            CommonUtils.getPrefValue(
                holder.itemView.context!!,
                PrefConstants.LATITUDE
            ).toDouble(),
            CommonUtils.getPrefValue(holder.itemView.context!!, PrefConstants.LONGITUDE).toDouble(),
            resModel.latitude.toDouble(),
            resModel.longitude.toDouble()
        )} ${holder.itemView.context!!.getString(R.string.km)}"


        if ((resModel.fullTime == "0")) {
            resModel.closeToShow =
                "${holder.itemView.context!!.getString(R.string.closes)} ${CommonUtils.getFormattedTimeOrDate(
                    resModel.closingTime,
                    "HH:mm:ss",
                    "hh:mm aa"
                )}"
        } else {
            resModel.closeToShow = holder.itemView.context!!.getString(R.string.open_24_hours)
        }

        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context!!)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        Picasso.get()
            .load(resModel.image).placeholder(
                circularProgressDrawable
            )
            .error(
                circularProgressDrawable
            )
            .into(holder.binding.img)

        if (resModel.items.size >= 4) {
            Picasso.get()
                .load(resModel.items[0].image).placeholder(
                    circularProgressDrawable
                )
                .error(
                    circularProgressDrawable
                )
                .into(holder.binding.img1)
            Picasso.get()
                .load(resModel.items[1].image).placeholder(
                    circularProgressDrawable
                )
                .error(
                    circularProgressDrawable
                )
                .into(holder.binding.img2)
            Picasso.get()
                .load(resModel.items[2].image).placeholder(
                    circularProgressDrawable
                )
                .error(
                    circularProgressDrawable
                )
                .into(holder.binding.img3)

            Picasso.get()
                .load(resModel.items[3].image).placeholder(
                    circularProgressDrawable
                )
                .error(
                    circularProgressDrawable
                )
                .into(holder.binding.img4)
        } else if (resModel.items.size == 3) {
            Picasso.get()


                .load(resModel.items[0].image).placeholder(
                    circularProgressDrawable
                )
                .error(
                    circularProgressDrawable
                )
                .into(holder.binding.img1)
            Picasso.get()
                .load(resModel.items[1].image).placeholder(
                    circularProgressDrawable
                )
                .error(
                    circularProgressDrawable
                )
                .into(holder.binding.img2)
            Picasso.get()
                .load(resModel.items[2].image).placeholder(
                    circularProgressDrawable
                )
                .error(
                    circularProgressDrawable
                )
                .into(holder.binding.img3)
            holder.binding.img4.visibility = View.GONE
        } else if (resModel.items.size == 2) {
            Picasso.get()
                .load(resModel.items[0].image).placeholder(
                    circularProgressDrawable
                )
                .error(
                    circularProgressDrawable
                )
                .into(holder.binding.img1)
            Picasso.get()
                .load(resModel.items[1].image).placeholder(
                    circularProgressDrawable
                )
                .error(
                    circularProgressDrawable
                )
                .into(holder.binding.img2)
            holder.binding.img3.visibility = View.GONE
            holder.binding.img4.visibility = View.GONE
        } else if (resModel.items.size == 1) {
            Picasso.get()
                .load(resModel.items[0].image).placeholder(
                    circularProgressDrawable
                )
                .error(
                    circularProgressDrawable
                )
                .into(holder.binding.img1)
            holder.binding.img2.visibility = View.GONE
            holder.binding.img3.visibility = View.GONE
            holder.binding.img4.visibility = View.GONE
        } else if (resModel.items.size == 0) {
            holder.binding.img1.visibility = View.GONE
            holder.binding.img2.visibility = View.GONE
            holder.binding.img3.visibility = View.GONE
            holder.binding.img4.visibility = View.GONE
        }


//        holder.binding.rcImages.adapter = ImagesAdapter(resModel, iRecyclerItemClick)
//
//        holder.binding.rcImages.setOnClickListener {
//            iRecyclerItemClick.onItemClick(resModel)
//        }

        holder.bind(resModel, iRecyclerItemClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.item_rest_search,
            parent,
            false
        )
        return ViewHolder(binding as ItemRestSearchBinding)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class ViewHolder(
        var binding: ItemRestSearchBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: NewDashboardModel.AllRestautants,
            listener: IRecyclerItemClick
        ) {
            binding.restaurantModel = item
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
}