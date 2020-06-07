package com.rvtechnologies.grigora.view.ui.orders

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.RoutesModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.network.AnimateMarker
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.utils.AppConstants.MESSAGE
import com.rvtechnologies.grigora.utils.AppConstants.NOTIFICATION_TYPE
import com.rvtechnologies.grigora.utils.AppConstants.ORDER_ID
import com.rvtechnologies.grigora.utils.AppConstants.PREPARING_TIME_RESTAURANT
import com.rvtechnologies.grigora.utils.AppConstants.TYPE
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.orders.adapter.OrderItemAdapter
import com.rvtechnologies.grigora.view.ui.orders.order_denied.OrderDeniedFragment
import com.rvtechnologies.grigora.view.ui.orders.order_denied.OrderDeniedRiderNotAvailable
import com.rvtechnologies.grigora.view.ui.rating.RateDriverDialogFragment
import com.rvtechnologies.grigora.view_model.OrderDetailsViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_map_icon.view.*
import kotlinx.android.synthetic.main.order_details_fragment.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URISyntaxException
import java.net.URL
import kotlin.random.Random


class OrderDetailsFragment : Fragment(), OnMapReadyCallback, RateDriverDialogFragment.DriverRate,
    IRecyclerItemClick {
    private var stop = false
    private var type = 0
    private var message = ""
    private lateinit var mMap: GoogleMap
    private var orderItemModel: OrderItemModel? = null
    private var receiver: BroadcastReceiver? = null
    lateinit var sheetBehavior: BottomSheetBehavior<View>
    var oldStatus = -1
    lateinit var timer: CountDownTimer
    private var mSocket: Socket? = null
    var isGroupOrder = false
    var isPickUp = false
    private lateinit var viewModel: OrderDetailsViewModel
    var DRIVER_TAG = "driver"
    var RESTAURANT_TAG = "restaurant"
    var MINE_TAG = "mine"
    var driverMarker: Marker? = null
    var animateMarker: AnimateMarker? = null
    var listOfMarker = ArrayList<Marker>()

    companion object {
        fun newInstance() = OrderDetailsFragment()
    }

    private val onOrderId = Emitter.Listener { args ->
        activity!!.runOnUiThread {

        }
    }

    var onConnect = object : Emitter.Listener {
        override fun call(vararg args: Any?) {
            Log.e("OrderDetail ", "connected")
        }
    }

    var onError = object : Emitter.Listener {
        override fun call(vararg args: Any?) {
            Log.e("OrderDetail ", "Error" + args[0])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        animateMarker = AnimateMarker()
        animateMarker!!.driverMarkerAnimFinished = true

        viewModel = ViewModelProviders.of(this).get(OrderDetailsViewModel::class.java)
        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

        if (arguments != null) {
            val orderId = arguments?.getInt(AppConstants.ORDER_ID).toString()
            viewModel.orderId.value = orderId
        }

        viewModel.orderItemRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                orderItemModel = response.data as OrderItemModel

                handleResponse(orderItemModel!!)
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel.isLoading.observe(this,
            Observer { response ->
                if (response) {
                    CommonUtils.showLoader(activity!!, getString(R.string.loading))
                } else {
                    CommonUtils.hideLoader()
                }
            })

        viewModel.cancelOrderRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                (activity as MainActivity).clearStack()
                (activity as MainActivity).selectedNavigation(R.id.dashBoardFragment)
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel.ratingResult.observe(this, Observer {
            if (it is CommonResponseModel<*>) {
                (activity as MainActivity).clearStack()
                (activity as MainActivity).selectedNavigation(R.id.dashBoardFragment)
            } else {
                CommonUtils.showMessage(parentView, it.toString())
            }
        })

        viewModel.completePickupOrderRes.observe(this, Observer {
            var congDialog = CongDialog(this)
            congDialog.isCancelable = false
            congDialog.show(childFragmentManager, "")
        })

        viewModel.changeDeliveryToPickup.observe(this, Observer { res ->
            if (res is CommonResponseModel<*> && type == 4) {
                if (res.status!!) {
                    viewModel.getOrderDetails()
                } else CommonUtils.showMessage(
                    parentView,
                    res.message!!
                )
            } else
                CommonUtils.showMessage(
                    parentView,
                    res.toString()!!
                )

        })

        viewModel.driverUpdateRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                var data = response.data as OrderItemModel
                orderItemModel = data
                if (data.driverId != null) {
                    updateOwnLocation()
                    viewModel.getEstimatedTime(
                        "${CommonUtils.getPrefValue(
                            context!!,
                            PrefConstants.LATITUDE
                        )},${CommonUtils.getPrefValue(context!!, PrefConstants.LONGITUDE)}",
                        "${data.driverLat},${data.driverLong}"
                    )
//                    setDriverMarker(data.driverLat.toDouble(), data.driverLong.toDouble())
                }
            }
        })

        viewModel.driver.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                var data = response.data as OrderItemModel
                orderItemModel = data
                setDriverMarker()
                handleResponse(orderItemModel!!)

                if (orderItemModel?.orderStatus == 4) {
                    startGettingDriverUpdates()
                }
            }
        })

        viewModel.driverLocationUpdateRes.observe(this, Observer { response ->
            if (response is RoutesModel) {
                tv_status.text = getString(R.string.delivering_in)
                li_driver.visibility = View.VISIBLE
                tv_driver_name.text = orderItemModel?.driverName


                val circularProgressDrawable = CircularProgressDrawable(context!!)
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 30f
                circularProgressDrawable.start()

                Picasso.get()
                    .load(orderItemModel?.driverImage).placeholder(
                        circularProgressDrawable
                    )
                    .error(
                        circularProgressDrawable
                    )
                    .into(img_driver)


                updateTimer(response.routes[0].legs[0].duration.value)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.findFragmentById(R.id.mMap) is SupportMapFragment) {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.mMap) as SupportMapFragment

            mapFragment.getMapAsync(this)
        }

        bt_cancel.setOnClickListener {
            val builder = AlertDialog.Builder(activity!!, R.style.TimePickerTheme)
            //set title for alert dialog
            builder.setTitle(R.string.cancel_order)
            //set message for alert dialog
            builder.setMessage(R.string.cencel_desc)
            //performing positive action
            builder.setPositiveButton(R.string.continueTxt) { dialogInterface, which ->
                dialogInterface.dismiss()
                viewModel.cancelOrder()
            }

            //performing negative action
            builder.setNegativeButton(R.string.places_cancel) { dialogInterface, which ->
                dialogInterface.dismiss()
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()

        }


        sheetBehavior = BottomSheetBehavior.from(li_bottomsheet)
        var displayMetrics = DisplayMetrics()
        (activity as MainActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        sheetBehavior.peekHeight = (displayMetrics.heightPixels - (displayMetrics.heightPixels / 3))

        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        Log.d("", "hidden")
                        var displayMetrics = DisplayMetrics()
                        (activity as MainActivity).windowManager.defaultDisplay.getMetrics(
                            displayMetrics
                        )
                        sheetBehavior.peekHeight =
                            (displayMetrics.heightPixels - (displayMetrics.heightPixels / 1.6).toInt())

                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//                        5
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })


        li_complete.setOnClickListener {
            viewModel.completeOrder()
        }


        li_driver.setOnClickListener {
            if (isPickUp) {
                if (!orderItemModel?.restaurantPhone.isNullOrEmpty())
                    call(orderItemModel?.restaurantPhone!!)
            } else {
                if (!orderItemModel?.driverPhone.isNullOrEmpty())
                    call(orderItemModel?.driverPhone!!)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1!!.getStringExtra(ORDER_ID) == viewModel.orderId.value) {
//        0=>Waiting for confirmation
//        2=>accepted by restaurant,
//        3=>driver assigned,
//        9=>restaurant starts preparing  get time from notification
//        7=> order is almost ready,   stop prepation time/nill
//        4=>out of delivery,  get driver time
//        5=> deliverd,   stop driver time
//        1=> schedule order ,
//        6=>rejected by restaurant,

//        8=>cancelled by customer,
//        (notification type )11=> show elapsed time and reset counter
                    if (p1!!.getStringExtra(NOTIFICATION_TYPE).toInt() == 9) {
//                        restaurant accepted
                        updateTimer(p1!!.getIntExtra(PREPARING_TIME_RESTAURANT, 0) * 60)
                        tv_status.text = getString(R.string.preparing)
                    }

                    if (p1!!.getStringExtra(NOTIFICATION_TYPE).toInt() == 11) {
//                        restaurant requested for more time to prepare
                        updateTimer(p1!!.getIntExtra(PREPARING_TIME_RESTAURANT, 0) * 60)
                        tv_status.text = getString(R.string.preparing)
                        CommonUtils.showMessage(
                            parentView,
                            getString(R.string.restaurant_asked_more_time)
                        )
                    }

                    if (p1!!.getStringExtra(NOTIFICATION_TYPE).toInt() == 7) {
//                        restaurant have prepared order
                        tv_status.text = getString(R.string.preparing)
                        updateTimer(0)
                    }

                    if (p1!!.getStringExtra(NOTIFICATION_TYPE).toInt() == 6) {
                        type = p1!!.getStringExtra(TYPE).toInt()
                        message = p1!!.getStringExtra(MESSAGE)
                    }
                    updateStatus(p1!!.getStringExtra(NOTIFICATION_TYPE).toInt())
                }
            }
        }
        activity!!.registerReceiver(receiver, IntentFilter("com.rvtechnologies.grigora"))
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle(getString(R.string.order))
            (activity as MainActivity).lockDrawer(true)

            (activity as MainActivity).img_back.setOnClickListener {
                (activity as MainActivity).clearStack()
                (activity as MainActivity).selectedNavigation(R.id.ordersFragment)
            }
        }
        stop = false

        viewModel.getOrderDetails()
    }

    override fun onPause() {
        super.onPause()
        activity!!.unregisterReceiver(receiver)
        if (mSocket != null)
            mSocket?.disconnect()


        stop = true
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket?.off(Socket.EVENT_CONNECT, onConnect)
        mSocket?.off(Socket.EVENT_ERROR, onError)
        mSocket?.off(viewModel.orderId.value!!, onOrderId)
        stop = true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (CommonUtils.isDarkMode())
            googleMap.setMapStyle(
                MapStyleOptions(
                    getResources()
                        .getString(R.string.dark_mode_style)
                )
            )
        else
            googleMap.setMapStyle(
                MapStyleOptions(
                    getResources()
                        .getString(R.string.light_mode_style)
                )
            )

        mMap = googleMap

        viewModel.getOrderDetails()
        try {
            val mOptions = IO.Options()
            mOptions.query = "id=" + CommonUtils.getPrefValue(context, PrefConstants.ID)

            mSocket = IO.socket(ApiConstants.SOCKET_URL, mOptions)
            mSocket?.on(Socket.EVENT_CONNECT, onConnect)
            mSocket?.on(Socket.EVENT_ERROR, onError)
            mSocket?.on(viewModel.orderId.value!!, onOrderId)
            mSocket?.connect()

        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    override fun onDriverRateSubmit(
        rating: Float, goodReview: String,
        badReview: String, orderItemModel: OrderItemModel, tip: String
    ) {
        viewModel.rateDriver(
            orderItemModel?.driverId!!,
            rating.toString(),
            goodReview,
            badReview,
            tip
        )
        orderItemModel.is_driver_rated = "1"
        CommonUtils.savePrefs(context, PrefConstants.ORDER_TO_RATE, Gson().toJson(orderItemModel))
    }

    override fun onDriverRateCancel(orderItemModel: OrderItemModel) {
        CommonUtils.savePrefs(context, PrefConstants.ORDER_TO_RATE, Gson().toJson(orderItemModel))
    }

    override fun onItemClick(item: Any) {
        if (item is Int) {
            (activity as MainActivity).clearStack()
            (activity as MainActivity).selectedNavigation(R.id.dashBoardFragment)
        } else if (item is String) {
            if (item == "1") {
                if (type == 4) {
                    viewModel.changeDeliveryToPickup("2")
                }
                (activity as MainActivity).clearStack()
                (activity as MainActivity).selectedNavigation(R.id.dashBoardFragment)


            } else if (item == "2") {
                viewModel.changeDeliveryToPickup("1")
            } else if (item == "3") {
                viewModel.changeDeliveryToPickup("3")
            }
        }
    }

    private fun handleResponse(orderItemModel: OrderItemModel) {
        viewModel.orderItemModel.value = orderItemModel

        tv_order_id.text = "#".plus(orderItemModel?.id)
        tv_rest_name.text = orderItemModel?.restaurantName
        CommonUtils.loadImage(img_rest, orderItemModel?.restaurantImage)

//        check if it is group order or pickup order, so that ui can be adjusted according to that
        isGroupOrder = orderItemModel!!.groupOrder == "1"
        isPickUp = orderItemModel!!.orderType != "1"

//        handle what to show in call buttons
        if (isPickUp) {
            tv_5.text = getString(R.string.ready_for_pickup)
            li_last.visibility = GONE
            tv_rest_desc.text = orderItemModel?.deliveryAddress
//            it is pickup, so show restaurant details in call button

            li_driver.visibility = VISIBLE
            CommonUtils.loadImage(img_driver, orderItemModel?.restaurantImage)
            tv_driver_name.text = getString(R.string.call_rest)
        } else {
            tv_rest_desc.text = orderItemModel?.restaurant_cusines
//            it is delivery, so show driver details in call button
            if (!orderItemModel.driverName.isNullOrEmpty()) {
                li_driver.visibility = View.VISIBLE
                tv_driver_name.text = orderItemModel?.driverName
                CommonUtils.loadImage(img_driver, orderItemModel?.driverImage)
            }
        }
//        handle time to show and status
        handleTimes(orderItemModel.orderStatus!!, orderItemModel.timeRemaining.toInt()!!)

        rvOrderItems.adapter = OrderItemAdapter(orderItemModel?.orderDetails!!)
        tv_subtotal.text = "₦ " + orderItemModel?.priceBeforePromo
        tv_promotion.text =
            "₦ " + (orderItemModel?.priceBeforePromo!!.toDouble() - orderItemModel?.priceAfterPromo!!.toDouble())
        tv_deliveryfee.text = "₦ " + orderItemModel.deliveryFee
        tv_total.text =
            "₦ " + CommonUtils.getRoundedOff((orderItemModel?.priceAfterPromo.toDouble() + orderItemModel.deliveryFee.toDouble()))

        setLocations(
            orderItemModel?.startLat!!,
            orderItemModel?.startLong!!,
            orderItemModel?.endLat!!,
            orderItemModel?.endLong!!
        )
    }

    private fun updateStatus(orderStatus: Int?) {
        var deActivatedColor = 0
        var activatedColor = 0
        deActivatedColor = ContextCompat.getColor(context!!, R.color.textGrey)
        activatedColor = if (CommonUtils.isDarkMode()) {
            ContextCompat.getColor(context!!, R.color.white)
        } else {
            ContextCompat.getColor(context!!, R.color.textBlack)
        }

//        0,2,3,9,7,4,5

//        0=>Waiting for confirmation
//        2=>accepted by restaurant,
//        3=>driver assigned,
//        9=>restaurant starts preparing
//        7=> order is almost ready,
//        4=>out of delivery,
//        5=> deliverd,
//        1=> schedule order ,
//        6=>rejected by restaurant,

//        8=>cancelled by customer,

        if (orderStatus != oldStatus) {
            oldStatus = orderStatus!!
            bt_cancel.visibility = GONE
            when (orderStatus) {
                0 -> {
//    0 -> "Waiting for confirmation"
                    bt_cancel.visibility = VISIBLE
                    tv_2.setTextColor(deActivatedColor)
                    tv_3.setTextColor(deActivatedColor)
                    tv_4.setTextColor(deActivatedColor)
                    tv_5.setTextColor(deActivatedColor)
                    tv_6.setTextColor(deActivatedColor)

                    img_2.setImageResource(R.drawable.ic_tick_mark_grey)
                    img_3.setImageResource(R.drawable.ic_cooking_grey)
                    img_4.setImageResource(R.drawable.ic_cooking_time_grey)
                    img_5.setImageResource(R.drawable.ic_shopping_bag_grey)
                    img_6.setImageResource(R.drawable.ic_motorcycle_grey)
                }
                2 -> {
                    showDirections()
//                    accepted by restaurant
                    tv_2.setTextColor(activatedColor)
                    tv_3.setTextColor(deActivatedColor)
                    tv_4.setTextColor(deActivatedColor)
                    tv_5.setTextColor(deActivatedColor)
                    tv_6.setTextColor(deActivatedColor)
                    img_2.setImageResource(R.drawable.ic_tick_mark)
                    img_3.setImageResource(R.drawable.ic_cooking_grey)
                    img_4.setImageResource(R.drawable.ic_cooking_time_grey)
                    img_5.setImageResource(R.drawable.ic_shopping_bag_grey)
                    img_6.setImageResource(R.drawable.ic_motorcycle_grey)
                    var anim = AnimationUtils.loadAnimation(context!!, R.anim.shakeanimation)
                    img_2.startAnimation(anim)
                }
                3 -> {
                    showDirections()
                    //        3=>driver assigned,
                    viewModel.getDriver()
                }
                9 -> {
                    showDirections()

//                    restaurant starts preparing
                    tv_2.setTextColor(activatedColor)
                    tv_3.setTextColor(activatedColor)
                    tv_4.setTextColor(deActivatedColor)
                    tv_5.setTextColor(deActivatedColor)
                    tv_6.setTextColor(deActivatedColor)
                    img_2.setImageResource(R.drawable.ic_tick_mark)
                    img_3.setImageResource(R.drawable.ic_cooking)
                    img_4.setImageResource(R.drawable.ic_cooking_time_grey)
                    img_5.setImageResource(R.drawable.ic_shopping_bag_grey)
                    img_6.setImageResource(R.drawable.ic_motorcycle_grey)
                    var anim = AnimationUtils.loadAnimation(context!!, R.anim.shakeanimation)
                    img_3.startAnimation(anim)

                }
                4 -> {
                    showDirections()

                    //    4 -> "Order picked up by driver,order is now its way to you"
                    tv_2.setTextColor(activatedColor)
                    tv_3.setTextColor(activatedColor)
                    tv_4.setTextColor(activatedColor)
                    tv_5.setTextColor(activatedColor)
                    tv_6.setTextColor(activatedColor)

                    img_2.setImageResource(R.drawable.ic_tick_mark)
                    img_3.setImageResource(R.drawable.ic_cooking)
                    img_4.setImageResource(R.drawable.ic_cooking_time)
                    img_5.setImageResource(R.drawable.ic_shopping_bag)
                    img_6.setImageResource(R.drawable.ic_motorcycle)

                    var anim = AnimationUtils.loadAnimation(context!!, R.anim.shakeanimation)

                    img_6.startAnimation(anim)
                    if (orderItemModel?.driverId == null) {
                        viewModel.getDriver()
                    } else {
                        setDriverMarker()
                        startGettingDriverUpdates()
                    }
                }
                5 -> {

//    5 -> "Order completed Delivered by " + orderModel.driverName
                    if (isPickUp) {
                        var congDialog = CongDialog(this)
                        congDialog.isCancelable = false
                        congDialog.show(childFragmentManager, "")
                    } else
                        rateDriverAndSaveOrder()

                }
                6 -> {
                    //    6 -> "Rejected by Restaurant"
                    if (type == 4) {
                        var deniedFragment =
                            OrderDeniedRiderNotAvailable(
                                this
                            )
                        deniedFragment.show(childFragmentManager, "")
                    } else {
                        var deniedFragment =
                            OrderDeniedFragment(
                                type,
                                message,
                                this
                            )
                        deniedFragment.show(childFragmentManager, "")
                    }
                }
                7 -> {
                    tv_status.visibility = GONE
                    tv_est_delivery.visibility = GONE
                    showDirections()

//    7 -> "Order is ready"
                    tv_2.setTextColor(activatedColor)
                    tv_3.setTextColor(activatedColor)
                    tv_4.setTextColor(activatedColor)
                    tv_5.setTextColor(activatedColor)
                    tv_6.setTextColor(deActivatedColor)

                    img_2.setImageResource(R.drawable.ic_tick_mark)
                    img_3.setImageResource(R.drawable.ic_cooking)
                    img_4.setImageResource(R.drawable.ic_cooking_time)
                    img_5.setImageResource(R.drawable.ic_shopping_bag)
                    img_6.setImageResource(R.drawable.ic_motorcycle_grey)

                    if (isPickUp) {
                        li_complete.visibility = VISIBLE
                    }

                    var anim = AnimationUtils.loadAnimation(context!!, R.anim.shakeanimation)

                    img_4.startAnimation(anim)

                    var random = Random.nextLong(10000)

                    Handler()
                        .postDelayed({
                            if (img_5 != null)
                                img_5.startAnimation(anim)
                        }, random)
                }
                8 -> {
//    8 -> "Order cancelled by use"
                    var deniedFragment =
                        OrderDeniedFragment(
                            3,
                            "",
                            this
                        )
                    deniedFragment.show(childFragmentManager, "")

                }

                else -> getString(R.string.waiting_for_confirmation)
            }
        }
    }

    private fun handleTimes(status: Int, time: Int) {
        tv_status.visibility = VISIBLE
        tv_est_delivery.visibility = VISIBLE

        tv_status.text = ""
        tv_est_delivery.text = ""

//      restaurant starts preparing or restaurant requested for more time to prepare
        if (status == 9 || status == 11) {
            tv_status.text = getString(R.string.preparing)
            updateTimer(time)

        }

//       restaurant have prepared order
        if (orderItemModel?.orderStatus == 7) {
            updateTimer(0)
            tv_status.text = ""
        }

//        9=>restaurant starts preparing  get time from notification
        if (orderItemModel?.orderStatus == 4) {
            tv_status.text = ""
        }

        if (status == 6) {
            type = orderItemModel!!.cancel_type.toInt()
            when (type) {
                1 -> {
                    message = getString(R.string.cancelled_by_you)
                }
                2 -> {
                    message = getString(R.string.items_not_available)
                }
                3 -> {
                    message = getString(R.string.elapsed_time_cancelled)
                }

                4 -> {
                    message = getString(R.string.no_riders_at_the_moment)
                }
                5 -> {
                    message = getString(R.string.restaurant_not_available)
                }
            }
        }
        updateStatus(status)
    }

    private fun setLocations(
        startLat: Double,
        startLong: Double,
        endLat: Double,
        endLong: Double
    ) {
        val restLocation = LatLng(startLat, startLong)
//        mMap.addMarker(
//            MarkerOptions().position(start)
//        )
        val custLocation = LatLng(endLat, endLong)
//        mMap.addMarker(
//            MarkerOptions().position(end)
//        )

        setMineImage()
        setRestaurantImage()
        val builder = LatLngBounds.builder()
        builder.include(restLocation)
        builder.include(custLocation)
        val latLngBounds = builder.build()

        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
            latLngBounds,
            CommonUtils.convertDpToPixel(50, context!!)
        )
        mMap.moveCamera(cameraUpdate)
    }

    private fun rateDriverAndSaveOrder() {
        var rateDriverDialog = RateDriverDialogFragment(orderItemModel!!, this)
        rateDriverDialog.isCancelable = false
        rateDriverDialog.show(this.childFragmentManager, "")
    }

    private fun updateTimer(seconds: Int) {
        tv_status.visibility = VISIBLE
        tv_est_delivery.visibility = VISIBLE
        if (seconds == 0) {
            tv_est_delivery.text = "00:00:00"
        }

//        tv_est_delivery.text = minutes.toString()
        if (::timer.isInitialized) {
            timer.cancel()
        }
        if (seconds != 0)
            timer = object : CountDownTimer((seconds * 1000).toLong(), 1000) {
                override fun onFinish() {
                }

                override fun onTick(millisUntilFinished: Long) {

                    val p1 = (millisUntilFinished / 1000) % 60
                    var p2 = (millisUntilFinished / 1000) / 60
                    val p3 = p2 % 60
                    p2 /= 60

                    var pp2 = if (p2 > 9) p2.toString() else "0$p2"
                    var pp1 = if (p1 > 9) p1.toString() else "0$p1"
                    var pp3 = if (p3 > 9) p3.toString() else "0$p3"

                    if (tv_est_delivery != null) {
                        tv_est_delivery.text = "$pp2:$pp3:$pp1"
                    } else
                        timer.cancel()

                }
            }.start()

    }

    var time = 5000.toLong()
    private fun startGettingDriverUpdates() {

        Handler().postDelayed({
            viewModel.getDriverDetails()
            startGettingDriverUpdates()
            time = 30000
        }, time)
    }

    private fun setDriverMarker() {
        val latLng =
            LatLng(orderItemModel?.driverLat!!.toDouble(), orderItemModel?.driverLong!!.toDouble())
        if (driverMarker == null) {
            val height: Int = resources.getDimension(R.dimen._40sdp).toInt()
            val width: Int = resources.getDimension(R.dimen._40sdp).toInt()
            val b = (activity!!.resources
                .getDrawable(R.drawable.driver_d) as BitmapDrawable).bitmap

            val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
            val markerOptions_driver = MarkerOptions()
            markerOptions_driver.position(latLng)
            markerOptions_driver.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .anchor(0.5f, 0.5f).flat(true)
            driverMarker = mMap!!.addMarker(markerOptions_driver)
        }
    }

    fun updateOwnLocation() {
        var myLocation =
            LatLng(orderItemModel?.driverLat!!.toDouble(), orderItemModel?.driverLong!!.toDouble())
        if (driverMarker == null) {
            setDriverMarker()
            listOfMarker.add(0, driverMarker!!)
        }
        var rotation = if (driverMarker == null) 0.0F else driverMarker!!.getRotation()
        if (animateMarker!!.currentLng != null) {
            rotation =
                animateMarker!!.bearingBetweenLocations(animateMarker!!.currentLng, myLocation)
                    .toFloat()
        } else {
            rotation = animateMarker!!.bearingBetweenLocations(myLocation, myLocation).toFloat()
        }
        updateCarMarker(myLocation, driverMarker, rotation, "45")
    }

    fun updateCarMarker(newLocation: LatLng, marker: Marker?, rotation: Float, driverId: String) {
        val previousItemOfMarker = animateMarker!!.getLastLocationDataOfMarker(marker)
        val data_map = HashMap<String, String>()
        data_map["vLatitude"] = "" + newLocation.latitude
        data_map["vLongitude"] = "" + newLocation.longitude
        data_map["iDriverId"] = "" + driverId
        data_map["RotationAngle"] = "" + rotation
        data_map["LocTime"] = "" + System.currentTimeMillis()
        val location = Location("marker")
        location.latitude = newLocation.latitude
        location.longitude = newLocation.longitude
        if (animateMarker!!.toPositionLat["" + newLocation.latitude] == null && animateMarker!!.toPositionLat["" + newLocation.longitude] == null) {
            if (previousItemOfMarker["LocTime"] != null && previousItemOfMarker["LocTime"] != "") {
                val previousLocTime = parseLongValue(0, previousItemOfMarker["LocTime"]!!)
                val newLocTime = parseLongValue(0, data_map["LocTime"]!!)
                if (previousLocTime != 0L && newLocTime != 0L) {
                    if (newLocTime - previousLocTime > 0 && animateMarker!!.driverMarkerAnimFinished === false) {
                        animateMarker!!.addToListAndStartNext(
                            marker,
                            this.mMap,
                            location,
                            rotation,
                            1200F,
                            driverId,
                            data_map["LocTime"]
                        )
                    } else if (newLocTime - previousLocTime > 0) {
                        animateMarker!!.animateMarker(
                            marker,
                            this.mMap,
                            location,
                            rotation,
                            1200F,
                            driverId,
                            data_map["LocTime"]
                        )
                    }
                } else if ((previousLocTime == 0L || newLocTime == 0L) && animateMarker!!.driverMarkerAnimFinished === false) {
                    animateMarker!!.addToListAndStartNext(
                        marker,
                        this.mMap,
                        location,
                        rotation,
                        1200F,
                        driverId,
                        data_map["LocTime"]
                    )
                } else {
                    animateMarker!!.animateMarker(
                        marker,
                        this.mMap,
                        location,
                        rotation,
                        1200F,
                        driverId,
                        data_map["LocTime"]
                    )
                }
            } else if (animateMarker!!.driverMarkerAnimFinished === false) {
                animateMarker!!.addToListAndStartNext(
                    marker,
                    this.mMap,
                    location,
                    rotation,
                    1200F,
                    driverId,
                    data_map["LocTime"]
                )
            } else {
                animateMarker!!.animateMarker(
                    marker,
                    this.mMap,
                    location,
                    rotation,
                    1200F,
                    driverId,
                    data_map["LocTime"]
                )
            }
        }
    }

    fun parseLongValue(defaultValue: Long, strValue: String): Long {
        return try {
            strValue.toLong()
        } catch (e: java.lang.Exception) {
            defaultValue
        }
    }

    private fun setMineImage() {
        val latLng =
            LatLng(orderItemModel?.endLat!!.toDouble(), orderItemModel?.endLong!!.toDouble())

        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))


        if (!CommonUtils.getPrefValue(context!!, PrefConstants.IMAGE).isNullOrEmpty())
            loadMarkerImage(
                latLng,
                CommonUtils.getPrefValue(context!!, PrefConstants.IMAGE),
                MINE_TAG
            )
        else {
            val height: Int = resources.getDimension(R.dimen._40sdp).toInt()
            val width: Int = resources.getDimension(R.dimen._40sdp).toInt()
            val b = (activity!!.resources
                .getDrawable(R.drawable.ic_user) as BitmapDrawable).bitmap

            val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
            val markerOptions_driver = MarkerOptions()
            markerOptions_driver.position(latLng)
            markerOptions_driver.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                .anchor(0.5f, 0.5f).flat(true)
            driverMarker = mMap!!.addMarker(markerOptions_driver)
//            loadMarkerImage(latLng, R.drawable.ic_user, MINE_TAG)
        }
    }

    private fun setRestaurantImage() {
        val latLng =
            LatLng(orderItemModel?.startLat!!.toDouble(), orderItemModel?.startLong!!.toDouble())
        loadMarkerImage(latLng, orderItemModel?.restaurantImage!!, RESTAURANT_TAG)
    }

    private fun loadMarkerImage(latlng: LatLng, image: Any, tag: String) {
        if (image is Int) {
            Picasso.get()
                .load(image).placeholder(R.drawable.ic_user).error(R.drawable.ic_user)
                .into(object : com.squareup.picasso.Target {
                    override
                    fun onBitmapLoaded(bitmap: Bitmap?, loadedFrom: LoadedFrom?) {
                        var b = bitmap!!.scale(40, 40)

                        val marker: Marker = mMap.addMarker(
                            MarkerOptions()
                                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        b
                                    )
                                )
                                .position(latlng)
                        )
                        marker.tag = tag
                        //                    vendorMarker.add(marker)
                    }

                    override
                    fun onPrepareLoad(drawable: Drawable?) {
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                    }
                })
        } else if (image is String) {
            Picasso.get()
                .load(image).placeholder(R.drawable.ic_user).error(R.drawable.ic_user)
                .into(object : com.squareup.picasso.Target {
                    override
                    fun onBitmapLoaded(bitmap: Bitmap?, loadedFrom: LoadedFrom?) {
                        val marker: Marker = mMap.addMarker(
                            MarkerOptions()
                                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        getMarkerBitmapFromView(
                                            bitmap!!
                                        )
                                    )
                                )
                                .position(latlng)
                        )
                        marker.tag = tag
                        //                    vendorMarker.add(marker)
                    }

                    override
                    fun onPrepareLoad(drawable: Drawable?) {
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                    }
                })
        }
    }

    private fun getMarkerBitmapFromView(
        resId: Bitmap
    ): Bitmap? {
        var view = LayoutInflater.from(context).inflate(
            R.layout.custom_map_icon,
            null,
            false
        )


        view.profile_image.setImageBitmap(resId)
        view.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = view.background
        drawable?.draw(canvas)
        view.draw(canvas)
        return getCroppedBitmap(returnedBitmap)
    }

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            bitmap.width / 2.toFloat(), bitmap.height / 2.toFloat(),
            bitmap.width / 2.toFloat(), paint
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }

    fun call(number: String) {
        if (!number.isNullOrEmpty())
            Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number))
                        startActivity(intent)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest,
                        token: PermissionToken
                    ) {

                    }
                }).check()
    }


    // Fetches data from url passed
    private inner class FetchUrl : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg url: String): String {

            // For storing data from web service
            var data = ""

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0])
                Log.d("Background Task data", data)
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }

            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            val parserTask = ParserTask()
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)

        }
    }

    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        // Parsing the data in non-ui thread
        override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {

            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null

            try {
                jObject = JSONObject(jsonData[0])
                Log.d("ParserTask", jsonData[0])
                val parser = DataParser()
                Log.d("ParserTask", parser.toString())

                // Starts parsing data
                routes = parser.parse(jObject)
                Log.d("ParserTask", "Executing routes")
                Log.d("ParserTask", routes!!.toString())

            } catch (e: Exception) {
                Log.d("ParserTask", e.toString())
                e.printStackTrace()
            }

            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {
            var points: ArrayList<LatLng>
            var lineOptions: PolylineOptions? = null
            // Traversing through all the routes
            for (i in result.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()
                // Fetching i-th route
                val path = result[i]
                // Fetching all the points in i-th route
                for (j in path.indices) {
                    val point = path[j]
                    val lat = java.lang.Double.parseDouble(point["lat"]!!)
                    val lng = java.lang.Double.parseDouble(point["lng"]!!)
                    val position = LatLng(lat, lng)
                    points.add(position)
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points)
                lineOptions.width(12f)
                if (CommonUtils.isDarkMode())
                    lineOptions.color(Color.WHITE)
                else
                    lineOptions.color(Color.BLACK)
                Log.d("onPostExecute", "onPostExecute lineoptions decoded")
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap!!.addPolyline(lineOptions)
            } else {
                Log.d("onPostExecute", "without Polylines drawn")
            }
        }
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection
            // Connecting to url
            urlConnection!!.connect()
            // Reading data from url
            iStream = urlConnection!!.getInputStream()
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (line != null) {
                try {
                    line = br.readLine()
                    sb.append(line)
                } catch (e: java.lang.Exception) {
                    line = null
                    Log.e("Exp", "HEHHEHEHE")
                }
            }

            data = sb.toString()
            Log.d("downloadUrl", data)
            br.close()

        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    private fun showDirections() {
        val url = getDirectionsUrl(
            LatLng(orderItemModel?.startLat!!, orderItemModel?.startLong!!),
            LatLng(orderItemModel?.endLat!!, orderItemModel?.endLong!!)
        )
        Log.d("onMapClick", url.toString())
        val FetchUrl = FetchUrl()

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url)
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        // Sensor enabled
        val sensor = "sensor=false"
        val mode = "mode=driving"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor&$mode"
        val key = "key=" + resources.getString(R.string.google_api_key)
        // Output format
        val output = "json"
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&$key"
    }

    inner class DataParser {
        /** Receives a JSONObject and returns a list of lists containing latitude and longitude  */
        fun parse(jObject: JSONObject): List<List<HashMap<String, String>>> {
            val routes = ArrayList<ArrayList<HashMap<String, String>>>()
            val jRoutes: JSONArray
            var jLegs: JSONArray
            var jSteps: JSONArray
            try {
                jRoutes = jObject.getJSONArray("routes")
                /** Traversing all routes  */
                for (i in 0 until jRoutes.length()) {
                    jLegs = (jRoutes.get(i) as JSONObject).getJSONArray("legs")
                    val path = ArrayList<HashMap<String, String>>()
                    /** Traversing all legs  */
                    for (j in 0 until jLegs.length()) {
                        jSteps = (jLegs.get(j) as JSONObject).getJSONArray("steps")
                        /** Traversing all steps  */
                        for (k in 0 until jSteps.length()) {
                            var polyline = ""
                            polyline =
                                ((jSteps.get(k) as JSONObject).get("polyline") as JSONObject).get("points") as String
                            val list = decodePoly(polyline)
                            /** Traversing all points  */
                            for (l in list.indices) {
                                val hm = HashMap<String, String>()
                                hm.put("lat", list[l].latitude.toString())
                                hm.put("lng", list[l].longitude.toString())
                                path.add(hm)
                            }
                        }
                        routes.add(path)
                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: Exception) {
            }
            return routes
        }

        /**
         * Method to decode polyline points
         * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         */
        private fun decodePoly(encoded: String): List<LatLng> {

            val poly = ArrayList<LatLng>()
            var index = 0
            val len = encoded.length
            var lat = 0
            var lng = 0

            while (index < len) {
                var b: Int
                var shift = 0
                var result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lat += dlat

                shift = 0
                result = 0
                do {
                    b = encoded[index++].toInt() - 63
                    result = result or (b and 0x1f shl shift)
                    shift += 5
                } while (b >= 0x20)
                val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
                lng += dlng

                val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
                poly.add(p)
            }
            return poly
        }
    }
}
