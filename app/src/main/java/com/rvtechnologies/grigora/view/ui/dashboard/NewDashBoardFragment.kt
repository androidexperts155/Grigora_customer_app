package com.rvtechnologies.grigora.view.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.FilteredPrice
import com.rvtechnologies.grigora.model.PriceFilterModel
import com.rvtechnologies.grigora.model.SelectedRating
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.DashboardAdapter
import com.rvtechnologies.grigora.view_model.NewDashBoardViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.new_dash_board_fragment.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class NewDashBoardFragment : Fragment(), IRecyclerItemClick {

    private lateinit var viewModel: NewDashBoardViewModel
    var map = HashMap<String, Any>()
    lateinit var newDashboardModel: NewDashboardModel
    lateinit var dashbordadapter: DashboardAdapter

    companion object {
        fun newInstance() = NewDashBoardFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NewDashBoardViewModel::class.java)

        viewModel.isLoading.observe(this, Observer { it ->
            if (it) {
                li_not_delivering.visibility = View.GONE

                shimmer_view.visibility = View.VISIBLE
                li_data.visibility = View.GONE
            } else {
                li_not_delivering.visibility = View.GONE

                shimmer_view.visibility = View.GONE
                li_data.visibility = View.VISIBLE
            }
        })

        viewModel.dashboardResult.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {

                newDashboardModel = response.data as NewDashboardModel
                if (newDashboardModel.allRestaurants.size == 0) {
                    li_data.visibility = View.GONE
                    li_not_delivering.visibility = View.VISIBLE

                    not_delivering.visibility = View.VISIBLE
                    went_wrong.visibility = View.GONE
                    tv_message.text = getString(R.string.not_delivering_message)

                } else {
                    li_data.visibility = View.VISIBLE
                    li_not_delivering.visibility = View.GONE

                    var temp = ArrayList<NewDashboardModel.CustomizedData>()

                    handleCart()
                    for (i in newDashboardModel.customizedData) {
                        if (i.restaurants.isEmpty()) {
                            temp.add(i)
                        }
                    }
                    saveWallet()
                    newDashboardModel.customizedData.removeAll(temp)

                    dashbordadapter = DashboardAdapter(newDashboardModel, this)
                    rc_dashboard.adapter = dashbordadapter
                }

            } else {
                li_data.visibility = View.GONE
                not_delivering.visibility = View.GONE
                li_not_delivering.visibility = View.VISIBLE
                went_wrong.visibility = View.VISIBLE
                tv_message.text = getString(R.string.oops_went_wrong)

            }
        })
    }

    private fun handleCart() {
        if (newDashboardModel.is_cart != null) {
            (activity as MainActivity).showFab()
            (activity as MainActivity).tv_restname.text =
                newDashboardModel.is_cart!!.restaurantName
            (activity as MainActivity).tv_items.text =
                newDashboardModel.is_cart!!.quantity.toString() + " Items"

            ((activity) as MainActivity).fab_cart.setOnClickListener {
                view?.findNavController()
                    ?.navigate(
                        R.id.action_dashBoardFragment_fragment_to_cart
                    )
            }
        }
    }

    private fun saveWallet() {
        CommonUtils.savePrefs(context, PrefConstants.WALLET, newDashboardModel.wallet)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CommonUtils.changeStatusBarColor(
            ContextCompat.getColor(context!!, R.color.lightGrey),
            activity as MainActivity
        )
        return inflater.inflate(R.layout.new_dash_board_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map["latitude"] = CommonUtils.getPrefValue(context!!, PrefConstants.LATITUDE)
        map["token"] = CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
        map["longitude"] = CommonUtils.getPrefValue(context!!, PrefConstants.LONGITUDE)
        map["filter_id"] = "0"
        map["cuisine_id"] = "0"
        map["user_id"] = CommonUtils.getPrefValue(context!!, PrefConstants.ID)

        viewModel.getDashboardData(map)
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).menuAddress()
            (activity as MainActivity).updateLocation()
            (activity as MainActivity).showBottomNavigation(0)
//            (activity as MainActivity).setRightIcon(R.drawable.ic_logout)
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).lockDrawer(true)
        }
    }

    override fun onItemClick(item: Any) {
        if (item is NewDashboardModel.Filter) {
            if (item.selectionType == "1") {
//                if(item.arrowClicked){
//                    var ratingDialog = RatingBarDialog(this, 0F, item)
//                    ratingDialog.show(this.childFragmentManager, "")
//                }
//                else
//                {
//                    var ratingDialog = RatingBarDialog(this, 0F, item)
//                    ratingDialog.show(this.childFragmentManager, "")
//                }
            } else if (item.selectionType == "2") {
                var list = ArrayList<PriceFilterModel>()
                if (item.multiSelected == null)
                    item.multiSelected = ""

                list.add(PriceFilterModel(item.multiSelected.contains("1"), "₦", "1"))
                list.add(PriceFilterModel(item.multiSelected.contains("2"), "₦₦", "2"))
                list.add(PriceFilterModel(item.multiSelected.contains("3"), "₦₦₦", "3"))
                list.add(PriceFilterModel(item.multiSelected.contains("4"), "₦₦₦₦", "4"))

                var filteredPrice = FilteredPrice(list)

                if (item.arrowClicked || item.multiSelected.isNullOrEmpty()) {
                    var priceDialog = PriceDialog(this, filteredPrice)
                    priceDialog.show(this.childFragmentManager, "")
                } else {
                    applyPriceFilter(item.multiSelected, !item.selected)
                }


            } else {
//                if (!item.selected)
                applyFilter(item.id.toString())
//                else
//                    applyFilter("filter_id", "0")
            }
        } else if (item is NewDashboardModel.Promo) {

        } else if (item is NewDashboardModel.Cuisine) {
            if (!item.selected)
                applyCuisineFilter("cuisine_id", item.id.toString())
            else
                applyCuisineFilter("cuisine_id", "0")

        } else if (item is NewDashboardModel.CustomizedData.Restaurant) {

            when (item.uiTpe) {
                "1" -> {
//                    RESTAURANT
                    val bundle = bundleOf(
                        AppConstants.RESTAURANT_ID to item.id,
                        AppConstants.RESTAURANT_PICKUP to item.pickup,
                        AppConstants.RESTAURANT_BOOKING to item.table_booking,
                        AppConstants.RESTAURANT_SEATES to item.no_of_seats,
                        AppConstants.RESTAURANT_CLOSING_TIME to item.closingTime,
                        AppConstants.RESTAURANT_OPENING_TIME to item.openingTime,
                        AppConstants.RESTAURANT_ALWAYS_OPEN to item.fullTime

                    )
                    view?.findNavController()
                        ?.navigate(
                            R.id.action_dashBoardFragment_fragment_to_restaurantDetailsParent,
                            bundle
                        )

                }
                "2" -> {
                    //                    TOP BRAND


                }
                "3" -> {
                    //                    CUISINE


                }
            }


//            if (newDashboardModel.customizedData[position - topSize].uiType.equals("1"))
//                RESTAURANTS_HORIZONTAL
//            else if (newDashboardModel.customizedData[position - topSize].uiType.equals("2"))
//                CUISINES
//            else
//                TOP_BRANDS
        } else if (item is NewDashboardModel.AllRestautants) {
            val bundle = bundleOf(
                AppConstants.RESTAURANT_ID to item.id,
                AppConstants.RESTAURANT_PICKUP to item.pickup,
                AppConstants.RESTAURANT_BOOKING to item.table_booking,
                AppConstants.RESTAURANT_SEATES to item.no_of_seats,
                AppConstants.RESTAURANT_CLOSING_TIME to item.closingTime,
                AppConstants.RESTAURANT_OPENING_TIME to item.openingTime,
                AppConstants.RESTAURANT_ALWAYS_OPEN to item.fullTime

            )
            view?.findNavController()
                ?.navigate(
                    R.id.action_dashBoardFragment_fragment_to_restaurantDetailsParent,
                    bundle
                )
        } else if (item is SelectedRating) {
            if (item.applyRating) {
                if (item.oldRating != item.newRating) {
//                    if (!item.filter.selected)
//                        applyFilter("filter_id", item.filter.id.toString())
//                    else
//                        applyFilter("filter_id", "0")
                }
            }
        } else if (item is FilteredPrice) {
            if (item.list.size > 0) {

                var name = ""

                for (d in item.list) {
                    name = if (name == "") {
                        d.value
                    } else {
                        name + ",${d.value}"
                    }
                }

                applyPriceFilter(name, true)

            } else {
                CommonUtils.showMessage(parentView, getString(R.string.no_range_selected))
            }
        }
    }

    private fun applyFilter(value: String) {
        val result: MutableList<List<String>> =
            Arrays.asList(map["filter_id"].toString().split(","))

        var list = ArrayList<String>(result[0])
        if (list.contains(value))
            (list).remove(value)
        else
            (list).add(value)


        if (list.size == 0) {
            list.add("0")
        } else
            if (list.contains("0"))
                list.remove("0")



        map["filter_id"] = list.joinToString(",")
        viewModel.getDashboardData(map)
    }

    private fun applyPriceFilter(filter: String, select: Boolean) {
        map["price_range"] = filter


        val result: MutableList<List<String>> =
            Arrays.asList(map["filter_id"].toString().split(","))

        var list = ArrayList<String>(result[0])
        if (select) {
            if (!list.contains("4"))
                list.add("4")
        } else
            (list).remove("4")


        if (list.size == 0) {
            list.add("0")
        } else
            if (list.contains("0"))
                list.remove("0")



        map["filter_id"] = list.joinToString(",")
        viewModel.getDashboardData(map)
//        applyFilter("4")
    }

    private fun applyCuisineFilter(key: String, value: String) {
        map[key] = value
        viewModel.getDashboardData(map)

    }


}
