package com.rvtechnologies.grigora.view.ui.orders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Ack
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.makesense.labs.curvefit.Curve
import com.makesense.labs.curvefit.CurveOptions
import com.makesense.labs.curvefit.impl.CurveManager
import com.makesense.labs.curvefit.interfaces.OnCurveDrawnCallback
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.RoutesModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.model.models.OrderStatusModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.utils.AppConstants.MESSAGE
import com.rvtechnologies.grigora.utils.AppConstants.NOTIFICATION_TYPE
import com.rvtechnologies.grigora.utils.AppConstants.ORDER_ID
import com.rvtechnologies.grigora.utils.AppConstants.PREPARING_TIME_RESTAURANT
import com.rvtechnologies.grigora.utils.AppConstants.TYPE
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.orders.adapter.OrderItemAdapter
import com.rvtechnologies.grigora.view.ui.rating.RateDriverDialogFragment
import com.rvtechnologies.grigora.view_model.OrderDetailsViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.order_details_fragment.*
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import kotlin.random.Random

class OrderDetailsFragment : Fragment(), OnMapReadyCallback, RateDriverDialogFragment.DriverRate,
    IRecyclerItemClick,
    OnCurveDrawnCallback {
    private var stop = false
    private var type = 0
    private var message = ""
    private lateinit var mMap: GoogleMap
    private var orderItemModel: OrderItemModel? = null
    private var receiver: BroadcastReceiver? = null
    private lateinit var curveManager: CurveManager
    lateinit var sheetBehavior: BottomSheetBehavior<View>
    var oldStatus = -1
    lateinit var timer: CountDownTimer


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
//        mMap.setMapStyle(MapS)

        viewModel.getOrderDetails()
        try {
            val mOptions = IO.Options()
            mOptions.query = "id=" + CommonUtils.getPrefValue(context, PrefConstants.ID)

            mSocket = IO.socket(ApiConstants.SOCKET_URL, mOptions)
            mSocket?.on(Socket.EVENT_CONNECT, onConnect)
            mSocket?.on(Socket.EVENT_ERROR, onError)
            mSocket?.on(viewModel.orderId.value!!, onOrderId)
            mSocket?.connect()
            mRecoverySocket = IO.socket(ApiConstants.SOCKET_URL, mOptions)
            mRecoverySocket?.connect()
            mRecoverySocket?.on(Socket.EVENT_CONNECT, onConnect1)
            mRecoverySocket?.on(Socket.EVENT_ERROR, onError1)
            curveManager = CurveManager(googleMap)
            curveManager.setOnCurveDrawnCallback(this)

        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }


    }

    var isGroupOrder = false
    var isPickUp = false

    companion object {
        fun newInstance() = OrderDetailsFragment()
    }

    private var mSocket: Socket? = null
    private var mRecoverySocket: Socket? = null

    private val onOrderId = Emitter.Listener { args ->
        activity!!.runOnUiThread {
            try {
                val data = Gson().fromJson(args[0].toString(), OrderStatusModel::class.java)
                if (data.orderStatus == 2 || data.orderStatus == 8 || data.orderStatus == 3 || data.orderStatus == 7) {
                    tv_est_delivery.text =
                        getDurationString(data?.dishRemainingTime!!)
//                        getString(R.string.preparation_time) + "   " + getDurationString(data?.dishRemainingTime!!)
                } else if (data.orderStatus == 4 || data.orderStatus == 5) {
                    tv_est_delivery.text =
                        getDurationString(data?.driverTime!!)
//                        getString(R.string.estimated_arrival) + "   " + getDurationString(data?.driverTime!!)
                }
//                val data =  args[0] as JSONObject
//                Log.e("data",data.toString())
////                val latitude = data.getDouble("latitude")
////                val longitude = data.getDouble("longitude")
////                val order_status = data.getInt("order_status")
////                val preparing_time = data.getInt("preparing_time")
////                val dishRemainingTime = data.getInt("dishRemainingTime")
////                val driverTime = data.getInt("driverTime")
////                orderItemModel?.orderStatus=order_status
////                updateStatus(order_status)
////                moveDriverTo(latitude, longitude)

                orderItemModel?.orderStatus = data.orderStatus
                updateStatus(data.orderStatus)
                data.latitude?.let { data.longitude?.let { it1 -> moveDriverTo(it, it1) } }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun getDurationString(seconds: Int): String {

        var hours = seconds / 3600
        var minutes = (seconds % 3600) / 60

//        return twoDigitString(hours) + " : " + twoDigitString(minutes)
        return twoDigitString(minutes)
    }

    fun twoDigitString(number: Int): String {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0$number"
        }

        return number.toString()
    }

    private fun moveDriverTo(latitude: Double, longitude: Double) {
        mMap.clear()
        val marker = MarkerOptions().position(LatLng(latitude, longitude))
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_icon)).title("Driver")
        mMap.addMarker(marker)
        if (marker == null) {
            return
        }

        setLocations(
            orderItemModel?.startLat!!,
            orderItemModel?.startLong!!,
            orderItemModel?.endLat!!,
            orderItemModel?.endLong!!
        )
//        val animation = ValueAnimator.ofFloat(0f, 100f)
//        var previousStep = 0f
//        val deltaLatitude = newLatLng.latitude - marker.position.latitude
//        val deltaLongitude = newLatLng.longitude - marker.position.longitude
//        animation.setDuration(1500)
//        animation.addUpdateListener { updatedAnimation ->
//            val deltaStep = updatedAnimation.getAnimatedValue() as Float - previousStep
//            previousStep = updatedAnimation.getAnimatedValue() as Float
//            marker.position = LatLng(marker.position.latitude + deltaLatitude * deltaStep * 1/100, marker.position.longitude + deltaStep * deltaLongitude * 1/100)
//        }
//        animation.start()
//        )
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

    var onConnect1 = object : Emitter.Listener {
        override fun call(vararg args: Any?) {
            recur()
            Log.e("EMITTER", "connected")
        }
    }

    var onError1 = object : Emitter.Listener {
        override fun call(vararg args: Any?) {
            Log.e("EMITTER ", "Error" + args[0])
        }
    }

    override fun onPause() {
        super.onPause()
        activity!!.unregisterReceiver(receiver)
        if (mSocket != null)
            mSocket?.disconnect()

        if (mRecoverySocket != null)
            mRecoverySocket?.disconnect()

        stop = true
    }

    private lateinit var viewModel: OrderDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(OrderDetailsViewModel::class.java)
        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
        if (arguments != null) {
            val orderId = arguments?.getInt(AppConstants.ORDER_ID).toString()
            viewModel.orderId.value = orderId
        }

        viewModel.orderItemRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                orderItemModel = response.data as OrderItemModel

                if (orderItemModel!!.groupOrder.equals("1")) {
                    isGroupOrder = true
                }

                isPickUp = orderItemModel!!.orderType != "1"

                if (isPickUp) {
                    li_last.visibility = View.GONE
                }

                tv_order_id.text = "#".plus(orderItemModel?.id)
                tv_rest_name.text = orderItemModel?.restaurantName
                if (orderItemModel?.preparingTime != null) {
                    updateTimer(orderItemModel?.timeRemaining?.toInt()!!)
                } else {
                    tv_status.visibility = View.GONE
                    tv_est_delivery.visibility = View.GONE
                    tv_mins.visibility = View.GONE
                }

                if (isPickUp)
                    tv_rest_desc.text = orderItemModel?.deliveryAddress
                else
                    tv_rest_desc.text = orderItemModel?.restaurant_cusines

                val circularProgressDrawable = CircularProgressDrawable(context!!)
                circularProgressDrawable.strokeWidth = 5f
                circularProgressDrawable.centerRadius = 30f
                circularProgressDrawable.start()

                Picasso.get()
                    .load(orderItemModel?.restaurantImage).placeholder(
                        circularProgressDrawable
                    )
                    .error(
                        circularProgressDrawable
                    )
                    .into(img_rest)




                if (isPickUp) {
                    li_driver.visibility = View.VISIBLE
                    val circularProgressDrawable = CircularProgressDrawable(context!!)
                    circularProgressDrawable.strokeWidth = 5f
                    circularProgressDrawable.centerRadius = 30f
                    circularProgressDrawable.start()

                    Picasso.get()
                        .load(orderItemModel?.restaurantImage).placeholder(
                            circularProgressDrawable
                        )
                        .error(
                            circularProgressDrawable
                        )
                        .into(img_driver)


                    li_driver.visibility = View.VISIBLE
                    tv_driver_name.text = getString(R.string.call_rest)
                } else if (!orderItemModel?.driverName.isNullOrEmpty() && !isPickUp) {
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
                }


//                tv_est_delivery.text =
//                    getString(R.string.preparation_time) + "   " + getDurationString(orderItemModel!!.timeRemaining.toInt())

                rvOrderItems.adapter = OrderItemAdapter(orderItemModel?.orderDetails!!)

                tv_subtotal.text = "₦ " + orderItemModel?.priceBeforePromo
                tv_promotion.text =
                    "₦ " + (orderItemModel?.priceBeforePromo!!.toDouble() - orderItemModel?.priceAfterPromo!!.toDouble())
                tv_deliveryfee.text = "₦ " + "20"
                tv_total.text = "₦ " + orderItemModel?.priceAfterPromo

//                CommonUtils.showMessage(parentView, response.message.toString())
                if (orderItemModel?.deliveryTime.isNullOrBlank())
                    orderItemModel?.deliveryTime = "-:-"

//                orderItemModel?.smallDescription =
//                    orderItemModel?.deliveryTime.plus(" | ")
//                        .plus(orderItemModel?.orderDetails?.size)
//                        .plus(" items,").plus(orderItemModel?.finalPrice)

                viewModel.orderItemModel.value = orderItemModel
//                binding?.orderViewModel = viewModel

                setLocations(
                    orderItemModel?.startLat!!,
                    orderItemModel?.startLong!!,
                    orderItemModel?.endLat!!,
                    orderItemModel?.endLong!!
                )
                if (orderItemModel?.orderStatus == 4) {
                    startGettingDriverUpdates()
                }
                updateStatus(orderItemModel?.orderStatus)
//                orderItemModel?.idToShow = "ORDER#".plus(orderItemModel?.id)

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
                view?.findNavController()?.popBackStack()
                view?.findNavController()?.popBackStack()
                view?.findNavController()?.popBackStack()
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
                }
            }

        })

        viewModel.driverUpdateRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                var data = response.data as OrderItemModel
                if (data.driverId != null) {
                    viewModel.getEstimatedTime(
                        "${CommonUtils.getPrefValue(
                            context!!,
                            PrefConstants.LATITUDE
                        )},${CommonUtils.getPrefValue(context!!, PrefConstants.LONGITUDE)}",
                        "${data.driverLat},${data.driverLong}"
                    )
                }
            }
        })


        viewModel.driverLocationUpdateRes.observe(this, Observer { response ->

            if (response is RoutesModel) {
                tv_status.text = getString(R.string.delivering_in)
                updateTimer(response.routes[0].legs[0].duration.value / 60 / 60)
            }
        })
    }

//    0 -> "Waiting for confirmation"
//    1 -> "Order scheduled"
//    2 -> "Order Accepted and being prepared"
//    8 -> "Rejected by Restaurant"
//    3 -> "Driver assigned"
//    4 -> "Order picked up by driver,order is now its way to you"
//    5 -> "Order completed Delivered by " + orderModel.driverName
//    6 -> "Rejected by Restaurant"
//    7 -> "Order almost ready"

    override fun onDestroy() {
        super.onDestroy()
        mSocket?.off(Socket.EVENT_CONNECT, onConnect)
        mSocket?.off(Socket.EVENT_ERROR, onError)
        mSocket?.off(viewModel.orderId.value!!, onOrderId)

        mRecoverySocket?.off(Socket.EVENT_CONNECT, onConnect)
        mRecoverySocket?.off(Socket.EVENT_ERROR, onError)
        mRecoverySocket?.off(viewModel.orderId.value!!, onOrderId)

        stop = true
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
                2, 3 -> {
                    //    2 -> "Order Accepted and being prepared"
                    //    3 -> "Driver assigned"

                    tv_2.setTextColor(activatedColor)
                    tv_3.setTextColor(activatedColor)
                    tv_4.setTextColor(deActivatedColor)
                    tv_5.setTextColor(deActivatedColor)
                    tv_6.setTextColor(deActivatedColor)
                    img_2.setImageResource(R.drawable.ic_tick_mark)
                    if (orderStatus == 3)
                        img_3.setImageResource(R.drawable.ic_cooking)
                    else
                        img_3.setImageResource(R.drawable.ic_cooking_grey)

                    img_4.setImageResource(R.drawable.ic_cooking_time_grey)
                    img_5.setImageResource(R.drawable.ic_shopping_bag_grey)
                    img_6.setImageResource(R.drawable.ic_motorcycle_grey)


                    var anim = AnimationUtils.loadAnimation(context!!, R.anim.shakeanimation)

                    if (orderStatus == 2)
                        img_2.startAnimation(anim)

                    var random = Random.nextLong(5000)

                    if (orderStatus == 3)
                        Handler()
                            .postDelayed({
                                img_3.startAnimation(anim)
                            }, random)

                }
                4 -> {
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
//TODO hit api to get estimated time
                }
                5 -> {
//    5 -> "Order completed Delivered by " + orderModel.driverName

                    rateDriverAndSaveOrder()

                }
                6 -> {
                    //    6 -> "Rejected by Restaurant"
                    var deniedFragment = OrderDeniedFragment(type, message, this)
                    deniedFragment.show(childFragmentManager, "")
                }
                7 -> {
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
                            img_5.startAnimation(anim)
                        }, random)


                }
                8 -> {
//    8 -> "Order cancelled by use"

                }

                else -> getString(R.string.waiting_for_confirmation)
            }
        }
    }

    private fun setLocations(
        startLat: Double,
        startLong: Double,
        endLat: Double,
        endLong: Double
    ) {
        curveManager = CurveManager(mMap)
        curveManager.setOnCurveDrawnCallback(this)
        val start = LatLng(startLat, startLong)
        mMap.addMarker(
            MarkerOptions().position(start)
        )
        val end = LatLng(endLat, endLong)
        mMap.addMarker(
            MarkerOptions().position(end)
        )

        val builder = LatLngBounds.builder()
        builder.include(start)
        builder.include(end)
        val latLngBounds = builder.build()

        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
            latLngBounds,
            CommonUtils.convertDpToPixel(50, context!!)
        )
        mMap.moveCamera(cameraUpdate)
//        showCurvedPolyline(start, end, 0.3)
        drawCurve(start, end)


    }

    fun drawCurve(
        p1: LatLng,
        p2: LatLng
    ) {
        // Create a CurveOptions object and add atleast two latlong points to it
        // You can set different options in CurveOptions object similar to PolyLineOptions
        var curveOptions = CurveOptions()
        curveOptions.add(p1)
        curveOptions.add(p2)
        curveOptions.color(Color.DKGRAY)
        curveOptions.setAlpha(0.2f)
        curveOptions.width(8F)
        var pattern = listOf(Dash(30F), Gap(30F))
        curveOptions.pattern(pattern)
        curveOptions.geodesic(false)
        curveOptions.startCap(RoundCap())
        curveOptions.endCap(SquareCap())

//        curveOptions.startCap(
//            CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.ic_checked), 10F))

//    mMap.addMarker(new MarkerOptions().position(sourceLatLng).anchor(0.5f, 1f));
//    map.addMarker(new MarkerOptions().position(destinationLatLng).icon(BitmapDescriptorFactory
//            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).anchor(0.5f, 1f));

//    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.64779202, 77.16562563), 14));

        // Draws curve asynchronously
        curveManager.drawCurveAsync(curveOptions)
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

        bt_cancel.setOnClickListener { viewModel.cancelOrder() }


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
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d("", "expanded")

//                        3
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d("", "collapsed")

//                        4
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        Log.d("", "dragging")

//                        1
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        Log.d("", "half expanded")

//                        1
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        Log.d("", "settling")

//                        cf
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })


        li_complete.setOnClickListener {
            viewModel.completeOrder()
        }

    }

    override fun onResume() {
        super.onResume()
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1!!.getStringExtra(ORDER_ID) == viewModel.orderId.value) {

                    if (p1!!.getStringExtra(NOTIFICATION_TYPE).toInt() == 2) {
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
        }
        stop = false
    }

    fun recur() {
        var jb = JSONObject()
        jb.put("orderId", viewModel.orderId.value!!.toInt())

        if (!stop)
            activity!!.runOnUiThread {
                Handler().postDelayed({
                    mRecoverySocket?.emit("getOrderData", jb, object : Ack {
                        override fun call(vararg args: Any?) {
                            Log.d("server", "received");
                        }
                    })
                    recur()
                }, 2000)
            }
    }

    private fun rateDriverAndSaveOrder() {
        var rateDriverDialog = RateDriverDialogFragment(orderItemModel!!, this)
        rateDriverDialog.isCancelable = false
        rateDriverDialog.show(this.childFragmentManager, "")
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

    override fun onCurveDrawn(curve: Curve?) {
    }

    override fun onItemClick(item: Any) {
        if (item is Int) {
            view?.findNavController()
                ?.navigate(R.id.action_orderDetailsFragment_to_dashboard)
        } else if (item is String) {
            if (item == "1") {
                if (type == 4) {
                    viewModel.changeDeliveryToPickup("2")
                }
                view?.findNavController()
                    ?.navigate(R.id.action_orderDetailsFragment_to_dashboard)


            } else if (item == "2") {
                viewModel.changeDeliveryToPickup("1")
            }
        }
    }

    private fun updateTimer(seconds: Int) {
        tv_status.visibility = VISIBLE
        tv_est_delivery.visibility = VISIBLE
        tv_mins.visibility = GONE
        if (seconds == 0) {
            tv_est_delivery.text = "00:00:00"
        }

//        tv_est_delivery.text = minutes.toString()
        if (::timer.isInitialized) {
            timer.cancel()
        }
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

                tv_est_delivery.text = "$pp2:$pp3:$pp1"
            }
        }.start()

    }

    private fun startGettingDriverUpdates() {
        Handler().postDelayed({
            viewModel.getDriverDetails()
            startGettingDriverUpdates()
        }, 4000)
    }
}
