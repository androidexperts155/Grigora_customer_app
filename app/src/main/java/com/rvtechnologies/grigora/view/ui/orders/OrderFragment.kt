package com.rvtechnologies.grigora.view.ui.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.orders.adapter.OrderAdapter
import com.rvtechnologies.grigora.view.ui.rating.MealsDataDialogFragment
import com.rvtechnologies.grigora.view.ui.rating.RateDriverDialogFragment
import com.rvtechnologies.grigora.view.ui.rating.RestaurantRatingDialogFragment
import com.rvtechnologies.grigora.view_model.OrderViewModel
import kotlinx.android.synthetic.main.alert_login.view.*
import kotlinx.android.synthetic.main.order_fragment.*
import org.json.JSONObject

class OrderFragment : Fragment(), IRecyclerItemClick, RateDriverDialogFragment.DriverRate,
    MealsDataDialogFragment.MealsRate,
    RestaurantRatingDialogFragment.RestaurantRate {

    private var currentIndex: Int = 0

    companion object {
        val ARG_CURRENT = "arg_is_current"
        fun newInstance(current: Int) = OrderFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_CURRENT, current)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentIndex = it.getInt(ARG_CURRENT)
        }

        viewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)

        viewModel.orderListRes.observe(this, Observer { orderListResponse ->
            if (orderListResponse is CommonResponseModel<*>) {
                orderList.clear()
                orderList.addAll(orderListResponse.data as Collection<OrderItemModel>)
                rvOrders?.adapter?.notifyDataSetChanged()
            } else {
                CommonUtils.showMessage(parentView, orderListResponse.toString())
            }
            error.visibility = if (orderList.isEmpty()) VISIBLE else GONE
        })
        viewModel.reOrderRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    view?.findNavController()
                        ?.navigate(
                            R.id.action_ordersFragment_to_cartFragment
                        )
                } else
                    CommonUtils.showMessage(parentView, response.message!!)
            } else
                CommonUtils.showMessage(parentView, response.toString())
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                activity?.let { CommonUtils.showLoader(it, getString(R.string.loading)) }
            } else {
                CommonUtils.hideLoader()
            }
        })
    }

    private lateinit var viewModel: OrderViewModel
    var orderList = ArrayList<OrderItemModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!CommonUtils.isLogin()) {
            (activity as MainActivity).showLoginAlert(pop = true, id = R.id.dashBoardFragment)
        } else {
            if (currentIndex == 0) {
                viewModel.getCurrentOrder(CommonUtils.getPrefValue(context!!,PrefConstants.TOKEN))
            } else if (currentIndex == 1) {
                viewModel.getPastOrder(CommonUtils.getPrefValue(context!!,PrefConstants.TOKEN))
            } else {
                viewModel.getUpcomingOrders(CommonUtils.getPrefValue(context!!,PrefConstants.TOKEN))
            }

            rvOrders.adapter = OrderAdapter(orderList, this, currentIndex)
        }
    }




    override fun onItemClick(item: Any) {
        if (item is OrderItemModel) {
            when (currentIndex) {
                0 -> {
                    val bundle = bundleOf(AppConstants.ORDER_ID to item.orderDetails[0].orderId)
                    view?.findNavController()
                        ?.navigate(
                            R.id.action_ordersFragment_to_orderDetailsFragment2,
                            bundle
                        )
                }
                1 -> {
                    if (item.isReorder) {
                        viewModel.reOrder(
                            CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
                            item.id.toString()
                        )
                    } else if (item.is_driver_rated == "0") {
                        var rateDriverDialog = RateDriverDialogFragment(item, this)
                        rateDriverDialog.isCancelable = false
                        rateDriverDialog.show(this.childFragmentManager, "")
                    } else if (item.is_restaurant_rated == "0") {
                        var restaurantRatingDialogFragment =
                            RestaurantRatingDialogFragment(item, this)
                        restaurantRatingDialogFragment.isCancelable = false
                        restaurantRatingDialogFragment.show(this.childFragmentManager, "")
                    } else {
                        var mealsToRate = ArrayList<OrderItemModel.OrderDetail>()
                        for (meal in item.orderDetails) {
                            if (meal.is_item_rated == "0") {
                                mealsToRate.add(meal)
                            }
                        }

                        if (mealsToRate.size > 0) {
                            var mealsRatingDialogFragment =
                                MealsDataDialogFragment(mealsToRate, this)
                            mealsRatingDialogFragment.isCancelable = false
                            mealsRatingDialogFragment.show(this.childFragmentManager, "")
                        }
                    }
                }
                2 -> {
//                    TODO: handle click of upcoming orders

                }
            }


        }
    }

    override fun onDriverRateSubmit(
        rating: Float, goodReview: String,
        badReview: String, orderItemModel: OrderItemModel, tip: String
    ) {
        viewModel.rateDriver(
            token = CommonUtils.getPrefValue(context, PrefConstants.TOKEN),
            rating = rating.toString(),
            orderId = orderItemModel.id.toString(),
            driverId = orderItemModel.driverId,
            goodReview = goodReview,
            badReview = badReview,
            tip = tip
        )

        if (orderItemModel.is_restaurant_rated == "0") {
            var restaurantRatingDialogFragment =
                RestaurantRatingDialogFragment(orderItemModel, this)
            restaurantRatingDialogFragment.isCancelable = false
            restaurantRatingDialogFragment.show(this.childFragmentManager, "")
        }
    }

    override fun onDriverRateCancel(orderItemModel: OrderItemModel) {
//        show restaurant rating if not rated
        if (orderItemModel.is_restaurant_rated == "0") {
            var restaurantRatingDialogFragment =
                RestaurantRatingDialogFragment(orderItemModel, this)
            restaurantRatingDialogFragment.isCancelable = false
            restaurantRatingDialogFragment.show(this.childFragmentManager, "")
        }
    }

    override fun onrestaurantRateSubmit(
        rating: Float, goodReview: String,
        badReview: String, orderItemModel: OrderItemModel
    ) {
        viewModel.rateRestaurant(
            token = CommonUtils.getPrefValue(context, PrefConstants.TOKEN),
            rating = rating.toString(),
            orderId = orderItemModel.id.toString(),
            restaurantId = orderItemModel.restaurantId.toString(),
            goodReview = goodReview,
            badReview = badReview
        )

        var mealsToRate = ArrayList<OrderItemModel.OrderDetail>()
        for (meal in orderItemModel.orderDetails)
            if (meal.is_item_rated == "0")
                mealsToRate.add(meal)


        if (mealsToRate.size > 0) {
            var mealsRatingDialogFragment = MealsDataDialogFragment(mealsToRate, this)
            mealsRatingDialogFragment.isCancelable = false
            mealsRatingDialogFragment.show(this.childFragmentManager, "")
        }
    }

    override fun onRateRestaurantCancel(orderItemModel: OrderItemModel) {
        var mealsToRate = ArrayList<OrderItemModel.OrderDetail>()
        for (meal in orderItemModel.orderDetails)
            if (meal.is_item_rated == "0")
                mealsToRate.add(meal)


        if (mealsToRate.size > 0) {
            var mealsRatingDialogFragment = MealsDataDialogFragment(mealsToRate, this)
            mealsRatingDialogFragment.isCancelable = false
            mealsRatingDialogFragment.show(this.childFragmentManager, "")
        }
    }

    override fun onMealRateSubmit(ratedMeals: ArrayList<OrderItemModel.OrderDetail>) {
        var map = HashMap<String, HashMap<String, String>>()

        for (meal in ratedMeals) {
            var m = HashMap<String, String>()
            m.put("rating", meal.rating.toString())
            m.put("review", meal.review)

            map.put(meal.itemId.toString(), m)
        }

        viewModel.rateMeals(
            token = CommonUtils.getPrefValue(context, PrefConstants.TOKEN),
            orderId = ratedMeals[0].orderId.toString(),
            ratings = JSONObject(map as Map<*, *>).toString()
        )
    }

    override fun onMealRateCancel() {

    }
}

