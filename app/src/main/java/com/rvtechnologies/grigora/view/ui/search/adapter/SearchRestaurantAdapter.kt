package com.rvtechnologies.grigora.view.ui.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ItemCuisineSearchBinding
import com.rvtechnologies.grigora.databinding.ItemRestSearchBinding
import com.rvtechnologies.grigora.model.SearchCuisineModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.search.SearchItem
import com.squareup.picasso.Picasso


class SearchRestaurantAdapter(
    var restaurantList: ArrayList<SearchItem>,
    val iRecyclerItemClick: IRecyclerItemClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val RESTAURANT = 1
    private val CUISINE = 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RestaurantSearchViewHolder) {
            val resModel = restaurantList[position] as NewDashboardModel.AllRestautants

            resModel.reviewToShow =
                "(${resModel.reviews} ${holder.itemView.context!!.getString(R.string.reviews)})"

            resModel.distance = "${CommonUtils.calculateDistance(
                CommonUtils.getPrefValue(
                    holder.itemView.context!!,
                    PrefConstants.LATITUDE
                ).toDouble(),
                CommonUtils.getPrefValue(
                    holder.itemView.context!!,
                    PrefConstants.LONGITUDE
                ).toDouble(),
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
        } else {
            holder as CuisineSearchViewHolder
            val resModel = restaurantList[position] as SearchCuisineModel
            holder.binding.tvName.text = resModel.name

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


            holder.itemView.setOnClickListener {
                iRecyclerItemClick.onItemClick(resModel)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (restaurantList[position] is SearchCuisineModel)
            CUISINE
        else
            RESTAURANT
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (p1 == RESTAURANT) {
            val binding = DataBindingUtil.inflate<ViewDataBinding>(
                inflater,
                R.layout.item_rest_search,
                parent,
                false
            )
            return RestaurantSearchViewHolder(binding as ItemRestSearchBinding)
        } else {
            val binding = DataBindingUtil.inflate<ViewDataBinding>(
                inflater,
                R.layout.item_cuisine_search,
                parent,
                false
            )
            return CuisineSearchViewHolder(binding as ItemCuisineSearchBinding)
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class RestaurantSearchViewHolder(
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

    class CuisineSearchViewHolder(
        var binding: ItemCuisineSearchBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: SearchCuisineModel,
            listener: IRecyclerItemClick
        ) {
            binding.searchCuisineModel = item
            binding.executePendingBindings()

        }
    }

}