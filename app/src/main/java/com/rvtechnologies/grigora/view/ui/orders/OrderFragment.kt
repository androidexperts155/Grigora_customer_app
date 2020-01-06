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
import com.google.gson.JsonObject

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OrderDetails
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.orders.adapter.OrderAdapter
import com.rvtechnologies.grigora.view.ui.rating.MealsRatingDialogFragment
import com.rvtechnologies.grigora.view.ui.rating.RateDriverDialogFragment
import com.rvtechnologies.grigora.view.ui.rating.RestaurantRatingDialogFragment
import com.rvtechnologies.grigora.view_model.OrderViewModel
import kotlinx.android.synthetic.main.alert_login.view.*
import kotlinx.android.synthetic.main.order_fragment.*
import org.json.JSONObject

class OrderFragment : Fragment(), IRecyclerItemClick, RateDriverDialogFragment.DriverRate,
    MealsRatingDialogFragment.MealsRate,
    RestaurantRatingDialogFragment.RestaurantRate {

    private var isCurrent: Boolean = false

    companion object {
        val ARG_IS_CURRENT = "arg_is_current"
        fun newInstance(isCurrent: Boolean) = OrderFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_IS_CURRENT, isCurrent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isCurrent = it.getBoolean(ARG_IS_CURRENT)
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
        val token = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
        if (token.isBlank()) {
            showLoginAlert(activity as MainActivity?)
        } else {
            if (isCurrent) {
                viewModel.getCurrentOrder(token)
            } else {
                viewModel.getPastOrder(token)
            }
            rvOrders.adapter = OrderAdapter(orderList, this, isCurrent)
        }
    }

    fun showLoginAlert(activity: MainActivity?) {
        var alertDialog: AlertDialog? = null

        val dialogBuilder = activity?.let { AlertDialog.Builder(it) }
        if (activity is MainActivity && !activity.isDestroyed && alertDialog == null) {
            val inflater = activity.layoutInflater
            val dialogView = inflater.inflate(R.layout.alert_login, null)
            dialogBuilder?.setView(dialogView)
            dialogBuilder?.setCancelable(false)
            dialogView.btnLogin.setOnClickListener {
                alertDialog?.dismiss()
                toLogin()
            }
            dialogView.btnLater.setOnClickListener {
                alertDialog?.dismiss()
                activity.onBackPressed()
            }

            alertDialog = dialogBuilder?.create()
            alertDialog?.show()
        }
    }

    private fun toLogin() {
        view?.findNavController()
            ?.navigate(
                R.id.action_navigationCart_to_loginFragment2
            )
    }

    override fun onItemClick(item: Any) {
        if (item is OrderItemModel) {
            if (isCurrent) {
                val bundle = bundleOf(AppConstants.ORDER_ID to item.orderDetails[0].orderId)
                view?.findNavController()
                    ?.navigate(
                        R.id.action_ordersFragment_to_orderDetailsFragment2,
                        bundle
                    )
            } else {

                if (item.is_driver_rated == "0") {
                    var rateDriverDialog = RateDriverDialogFragment(item, this)
                    rateDriverDialog.isCancelable = false
                    rateDriverDialog.show(this.childFragmentManager, "")
                } else if (item.is_restaurant_rated == "0") {
                    var restaurantRatingDialogFragment = RestaurantRatingDialogFragment(item, this)
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
                        var mealsRatingDialogFragment = MealsRatingDialogFragment(mealsToRate, this)
                        mealsRatingDialogFragment.isCancelable = false
                        mealsRatingDialogFragment.show(this.childFragmentManager, "")
                    }
                }
            }
        }
    }

    override fun onDriverRateSubmit(rating: Float, orderItemModel: OrderItemModel) {
        viewModel.rateDriver(
            token = CommonUtils.getPrefValue(context, PrefConstants.TOKEN),
            rating = rating.toString(),
            orderId = orderItemModel.id.toString(),
            driverId = orderItemModel.driverId
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

    override fun onrestaurantRateSubmit(rating: Float, orderItemModel: OrderItemModel) {
        viewModel.rateRestaurant(
            token = CommonUtils.getPrefValue(context, PrefConstants.TOKEN),
            rating = rating.toString(),
            orderId = orderItemModel.id.toString(),
            restaurantId = orderItemModel.restaurantId.toString()
        )

        var mealsToRate = ArrayList<OrderItemModel.OrderDetail>()
        for (meal in orderItemModel.orderDetails)
            if (meal.is_item_rated == "0")
                mealsToRate.add(meal)


        if (mealsToRate.size > 0) {
            var mealsRatingDialogFragment = MealsRatingDialogFragment(mealsToRate, this)
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
            var mealsRatingDialogFragment = MealsRatingDialogFragment(mealsToRate, this)
            mealsRatingDialogFragment.isCancelable = false
            mealsRatingDialogFragment.show(this.childFragmentManager, "")
        }
    }

    override fun onMealRateSubmit(ratedMeals: ArrayList<OrderItemModel.OrderDetail>) {
        var map = HashMap<String, String>()

        for (meal in ratedMeals) {
            map.put(meal.itemId.toString(), meal.rating.toString())
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
