package com.rvtechnologies.grigora.view.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.ViewMore
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.new_dash_board_fragment.*

class DashboardAdapter(
    val newDashboardModel: NewDashboardModel,
    val iRecyclerItemClick: IRecyclerItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var FILTER = 1
    var OFFERS = 2
    var CATEGORIES = 3
    var CUISINES = 5
    var TOP_BRANDS = 6
    var RESTAURANTS_HORIZONTAL = 7
    var RESTAURANTS_VERTICAL = 8


    var topSize = 3

    override fun getItemViewType(position: Int): Int {

        return if (position == 0) {
            FILTER
        } else if (position == 1) {
            OFFERS
        } else if (position == 2) {
            CATEGORIES
        } else if (position > topSize - 1 && position < topSize + newDashboardModel.customizedData.size) {
            if (newDashboardModel.customizedData[position - topSize].uiType.equals("1"))
                RESTAURANTS_HORIZONTAL
            else if (newDashboardModel.customizedData[position - topSize].uiType.equals("2"))
                CUISINES
            else
                TOP_BRANDS
        } else
            RESTAURANTS_VERTICAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == FILTER) {
            Filters(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_dashboard,
                    parent,
                    false
                )
            )
        } else if (viewType == OFFERS) {
            Offers(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_dashboard,
                    parent,
                    false
                )
            )
        } else if (viewType == CATEGORIES) {
            Categories(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_dashboard,
                    parent,
                    false
                )
            )
        } else if (viewType == CUISINES) {
            Cuisines(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_dashboard,
                    parent,
                    false
                )
            )
        } else if (viewType == TOP_BRANDS) {
            TopBrands(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_dashboard,
                    parent,
                    false
                )
            )
        } else if (viewType == RESTAURANTS_HORIZONTAL) {
            RestaurantsHorizontal(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_dashboard,
                    parent,
                    false
                )
            )
        } else {
            RestaurantsVertical(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_dashboard,
                    parent,
                    false
                )
            )
        }

    }

    override fun getItemCount(): Int {
        return newDashboardModel.customizedData.size + topSize + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var layoutManager = LinearLayoutManager(holder.itemView.context)
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL)

        when (holder) {
            is Filters -> {
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                holder.rc_data.layoutManager = layoutManager
                holder.rel_title.visibility = View.GONE
                var adapter = FilterAdapter(newDashboardModel.filters, iRecyclerItemClick)
                holder.rc_data.layoutManager = layoutManager
                holder.rc_data.adapter = adapter
                CommonUtils.setOverScroll(holder.rc_data,1)

            }
            is Offers -> {
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL

                holder.rc_data.layoutManager = layoutManager
                holder.rel_title.visibility = View.GONE
                holder.rc_data.adapter = OfferAdapter(newDashboardModel.promos, iRecyclerItemClick)
                CommonUtils.setOverScroll(holder.rc_data,1)
            }
            is Categories -> {
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                holder.rc_data.layoutManager = layoutManager
                holder.rel_title.visibility = View.GONE
                holder.rc_data.adapter =
                    CategoriesAdapter(newDashboardModel.cuisines, iRecyclerItemClick)
                CommonUtils.setOverScroll(holder.rc_data,1)

            }
            is Cuisines -> {
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                holder.rc_data.layoutManager = layoutManager
                var customisedData = newDashboardModel.customizedData[position - topSize]
                holder.rel_title.visibility = View.VISIBLE
                if (customisedData.showAll == "1")
                    holder.tv_more.visibility = View.VISIBLE
                else
                    holder.tv_more.visibility = View.GONE

                holder.tv_title.text = customisedData.title

                holder.rc_data.adapter =
                    CuisineAdapter(
                        customisedData.restaurants,
                        iRecyclerItemClick,
                        customisedData.uiType
                    )
                CommonUtils.setOverScroll(holder.rc_data,1)

            }
            is TopBrands -> {
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL

                holder.rc_data.layoutManager = layoutManager

                var customisedData = newDashboardModel.customizedData[position - topSize]
                holder.rel_title.visibility = View.VISIBLE
                if (customisedData.showAll == "1")
                    holder.tv_more.visibility = View.VISIBLE
                else
                    holder.tv_more.visibility = View.GONE

                holder.tv_title.text = customisedData.title

                holder.rc_data.adapter = TopBrandAdapter(
                    customisedData.restaurants, newDashboardModel.min_kilo_meter,
                    newDashboardModel.base_delivery_fee, iRecyclerItemClick, customisedData.uiType
                )
                CommonUtils.setOverScroll(holder.rc_data,1)
            }
            is RestaurantsHorizontal -> {
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL

                holder.rc_data.layoutManager = layoutManager

                var customisedData = newDashboardModel.customizedData[position - topSize]

                holder.rel_title.visibility = View.VISIBLE
                if (customisedData.showAll == "1")
                    holder.tv_more.visibility = View.VISIBLE
                else
                    holder.tv_more.visibility = View.GONE

                holder.tv_title.text = customisedData.title

                holder.rc_data.adapter = DashboardRestaurantAdapter(
                    customisedData.restaurants,
                    newDashboardModel.min_kilo_meter,
                    newDashboardModel.base_delivery_fee, iRecyclerItemClick, customisedData.uiType
                )

                holder.tv_more.setOnClickListener {
                    iRecyclerItemClick.onItemClick(ViewMore(customisedData.id))
                }
                CommonUtils.setOverScroll(holder.rc_data,1)
            }
            is RestaurantsVertical -> {
                layoutManager.orientation = LinearLayoutManager.VERTICAL
                holder.rc_data.layoutManager = layoutManager


                holder.rel_title.visibility = View.VISIBLE
                holder.tv_more.visibility = View.GONE

                holder.tv_title.text = holder.tv_title.context.getString(R.string.all_restaurants)

                AppConstants.base_delivery_fee = newDashboardModel.base_delivery_fee
                AppConstants.min_kilo_meter = newDashboardModel.min_kilo_meter

                holder.rc_data.adapter = DashboardAllRestaurantAdapter(
                    newDashboardModel.allRestaurants,
                    newDashboardModel.min_kilo_meter,
                    newDashboardModel.base_delivery_fee, iRecyclerItemClick
                )
            }
        }
    }

    inner class Filters(view: View) : RecyclerView.ViewHolder(view) {
        var rel_title: RelativeLayout = view.findViewById(R.id.rel_title)
        var rc_data: RecyclerView = view.findViewById(R.id.rc_data)

    }

    inner class Offers(view: View) : RecyclerView.ViewHolder(view) {
        var rel_title: RelativeLayout = view.findViewById(R.id.rel_title)
        var rc_data: RecyclerView = view.findViewById(R.id.rc_data)
    }

    inner class Categories(view: View) : RecyclerView.ViewHolder(view) {
        var rel_title: RelativeLayout = view.findViewById(R.id.rel_title)
        var rc_data: RecyclerView = view.findViewById(R.id.rc_data)
    }

    inner class Cuisines(view: View) : RecyclerView.ViewHolder(view) {
        var rel_title: RelativeLayout = view.findViewById(R.id.rel_title)
        var tv_title: TextView = view.findViewById(R.id.tv_title)
        var tv_more: TextView = view.findViewById(R.id.tv_more)
        var rc_data: RecyclerView = view.findViewById(R.id.rc_data)
    }

    inner class TopBrands(view: View) : RecyclerView.ViewHolder(view) {
        var rel_title: RelativeLayout = view.findViewById(R.id.rel_title)
        var tv_title: TextView = view.findViewById(R.id.tv_title)
        var tv_more: TextView = view.findViewById(R.id.tv_more)
        var rc_data: RecyclerView = view.findViewById(R.id.rc_data)
    }

    inner class RestaurantsHorizontal(view: View) : RecyclerView.ViewHolder(view) {
        var rel_title: RelativeLayout = view.findViewById(R.id.rel_title)
        var tv_title: TextView = view.findViewById(R.id.tv_title)
        var tv_more: TextView = view.findViewById(R.id.tv_more)
        var rc_data: RecyclerView = view.findViewById(R.id.rc_data)
    }

    inner class RestaurantsVertical(view: View) : RecyclerView.ViewHolder(view) {
        var rel_title: RelativeLayout = view.findViewById(R.id.rel_title)
        var tv_title: TextView = view.findViewById(R.id.tv_title)
        var tv_more: TextView = view.findViewById(R.id.tv_more)
        var rc_data: RecyclerView = view.findViewById(R.id.rc_data)
    }
}