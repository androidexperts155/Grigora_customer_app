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
import androidx.navigation.Navigation
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.*
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.NewDashboardModel
import com.rvtechnologies.grigora.utils.*
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

    var getAllCart = true

    companion object {
        fun newInstance() = NewDashBoardFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppConstants.CART_RESTAURANT = ""
        AppConstants.CART_COUNT = 0


        getAllCart = CommonUtils.isLogin()

        viewModel = ViewModelProviders.of(this).get(NewDashBoardViewModel::class.java)
        GrigoraApp.getInstance().updateToken()




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


        viewModel.checkUnderLocationResult.observe(this,Observer{
            viewModel.getAllCart()
        })

        viewModel.dashboardResult.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                newDashboardModel = response.data as NewDashboardModel
                AppConstants.base_delivery_fee = newDashboardModel.base_delivery_fee
                AppConstants.min_kilo_meter = newDashboardModel.min_kilo_meter

                CommonUtils.savePrefs(
                    context,
                    PrefConstants.BASE_DELIVERY_FEE,
                    AppConstants.base_delivery_fee
                )
                CommonUtils.savePrefs(
                    context,
                    PrefConstants.MIN_KILO_METER,
                    AppConstants.min_kilo_meter
                )


                if (newDashboardModel.allRestaurants.size == 0) {
                    li_data.visibility = View.GONE
                    li_not_delivering.visibility = View.VISIBLE

                    not_delivering.visibility = View.VISIBLE
                    went_wrong.visibility = View.GONE
                    tv_message.text = getString(R.string.not_delivering_message)

//                    var pickupDialog = PickupDialog(this)
//                    pickupDialog.show(childFragmentManager, "")

                } else if (!newDashboardModel.driverStatus) {
                    var pickupDialog = PickupDialog(this)
                    pickupDialog.show(childFragmentManager, "")
                } else {

                    li_data.visibility = View.VISIBLE
                    li_not_delivering.visibility = View.GONE

                    var temp = ArrayList<NewDashboardModel.CustomizedData>()
                    if (newDashboardModel.notifications) {
                        (activity as MainActivity).setRightIcon(R.drawable.ic_have_notification)
                    } else {
                        (activity as MainActivity).setRightIcon(R.drawable.ic_no_notification)
                        (activity as MainActivity).img_right.visibility == View.GONE
                    }

                    handleCart()
                    for (i in newDashboardModel.customizedData) {
                        if (i.restaurants.isEmpty()) {
                            temp.add(i)
                        }
                    }
                    saveWallet()
                    newDashboardModel.customizedData.removeAll(temp)

                    var show=false
                    for(item in newDashboardModel.cuisines){
                        if(item.selected){
                            show=true
                            break
                        }
                    }

                    for(item in newDashboardModel.filters){
                        if(item.selected){
                            show=true
                            break
                        }
                    }

                    if(show){
                        tv_reset.visibility=View.VISIBLE
                    }
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

        viewModel.allCartResult.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!)
                    if (response.data is AllCartModel) {
                        var data = response.data as AllCartModel
                        if (data.login_cart != null && data.logout_cart != null) {
                            var f = PreviousCart(this, data)
                            f.show(childFragmentManager, "")
                        } else
                            viewModel.getDashboardData(map)

                    } else viewModel.getDashboardData(map)
                else viewModel.getDashboardData(map)

            }

        })

        viewModel.mergeCartResult.observe(this, Observer {
            viewModel.getDashboardData(map)
        })
    }

    private fun handleCart() {
        if (::newDashboardModel.isInitialized && newDashboardModel.is_cart != null) {
            AppConstants.CART_RESTAURANT = newDashboardModel.is_cart!!.restaurantName
            AppConstants.CART_COUNT = newDashboardModel.is_cart!!.quantity
            (activity as MainActivity).updateCartButton()
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
        tv_reset.visibility=View.GONE
        map["latitude"] = CommonUtils.getPrefValue(context!!, PrefConstants.LATITUDE)
//        map["token"] = CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
        map["longitude"] = CommonUtils.getPrefValue(context!!, PrefConstants.LONGITUDE)
        map["filter_id"] = "0"
        map["cuisine_id"] = "0"
        map["user_id"] = CommonUtils.getUid()

        if (getAllCart) {
            getAllCart = false
            viewModel.checkUnderLocation()
        } else
            viewModel.getDashboardData(map)

        li_search.setOnClickListener {
            view?.findNavController()
                ?.navigate(
                    R.id.action_dashBoardFragment_fragment_to_searchRestaurant
                )
        }

        tv_reset.setOnClickListener { reset() }
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).menuAddress()
            (activity as MainActivity).updateLocation()
            (activity as MainActivity).showBottomNavigation(0)


            (activity as MainActivity).img_right.setOnClickListener {
                if (CommonUtils.isLogin())
                    Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment)
                        .navigate(R.id.notifications)
                else
                    (activity as MainActivity).showLoginAlert()
            }
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).lockDrawer(true)
        }
        handleCart()

    }

    override fun onItemClick(item: Any) {
        if (item is AllCartModel) {
            viewModel.mergeCart(item.seleted)
        } else if (item is ViewMore) {
            val bundle = bundleOf(
                AppConstants.FILTER_ID to item.data.toString()
            )

            view?.findNavController()
                ?.navigate(
                    R.id.action_dashBoardFragment_fragment_to_commonViewAll, bundle
                )
        } else if (item is NewDashboardModel.Filter) {
            if (item.selectionType == "1") {
                if (!item.selected) {
                    var priceDialog = RatingBarDialog(this, 0.0F, item)
                    priceDialog.show(this.childFragmentManager, "")
                } else removeRatingFiler()
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
            val bundle = bundleOf(
                AppConstants.FILTER_ID to item.id.toString(),
                AppConstants.FOR_PROMO to true,
                AppConstants.TITLE to item.name
            )

            view?.findNavController()
                ?.navigate(
                    R.id.action_dashBoardFragment_fragment_to_commonViewAll, bundle
                )
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
                    //                    CUISINE
                    val bundle = bundleOf(
                        AppConstants.FILTER_ID to "121",
                        AppConstants.CUISINE_ID to item.id.toString()
                    )

                    view?.findNavController()
                        ?.navigate(
                            R.id.action_dashBoardFragment_fragment_to_commonViewAll, bundle
                        )


                }
                "3" -> {
                    //                    TOP BRAND

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
                    applyRatingFilter(item.newRating.toString())
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
        } else if (item is Int) {
            if (item == 1)
                (activity as MainActivity).selectedNavigation(R.id.pickupRestaurants)
            else
                viewModel.getDashboardData(map)
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

    private fun applyRatingFilter(filter: String) {
        map["rating_range"] = filter
        val result: MutableList<List<String>> =
            Arrays.asList(map["filter_id"].toString().split(","))


        var list = ArrayList<String>(result[0])
        if (!list.contains("1"))
            list.add("1")

        map["filter_id"] = list.joinToString(",")
        viewModel.getDashboardData(map)


    }

    private fun removeRatingFiler() {
        val result: MutableList<List<String>> =
            Arrays.asList(map["filter_id"].toString().split(","))

        var list = ArrayList<String>(result[0])
        if (list.contains("1"))
            list.remove("1")

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

    private fun reset() {
        tv_reset.visibility=View.GONE
        map.clear()
        map["latitude"] = CommonUtils.getPrefValue(context!!, PrefConstants.LATITUDE)
//        map["token"] = CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
        map["longitude"] = CommonUtils.getPrefValue(context!!, PrefConstants.LONGITUDE)
        map["filter_id"] = "0"
        map["cuisine_id"] = "0"
        map["user_id"] = CommonUtils.getUid()
        viewModel.getDashboardData(map)
    }
}
