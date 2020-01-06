package com.rvtechnologies.grigora.view.ui.orders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
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
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.OrderItemModel
import com.rvtechnologies.grigora.model.models.OrderStatusModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.utils.AppConstants.NOTIFICATION_TYPE
import com.rvtechnologies.grigora.utils.AppConstants.ORDER_ID
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.orders.adapter.OrderItemAdapter
import com.rvtechnologies.grigora.view.ui.rating.RateDriverDialogFragment
import com.rvtechnologies.grigora.view_model.OrderDetailsViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.order_details_fragment.*
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*

class OrderDetailsFragment : Fragment(), OnMapReadyCallback, RateDriverDialogFragment.DriverRate,
    OnCurveDrawnCallback {
    private var stop = false
    private lateinit var mMap: GoogleMap
    //    private var binding: OrderDetailsFragmentBinding? = null
    private var orderItemModel: OrderItemModel? = null
    private var preLatlng: LatLng? = null
    private var receiver: BroadcastReceiver? = null
    private lateinit var curveManager: CurveManager
    lateinit var sheetBehavior: BottomSheetBehavior<View>


    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setMapStyle(
            MapStyleOptions(
                resources.getString(R.string.style_json)
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
                        getString(R.string.preparation_time) + "   " + getDurationString(data?.dishRemainingTime!!)
                } else if (data.orderStatus == 4 || data.orderStatus == 5) {
                    tv_est_delivery.text =
                        getString(R.string.estimated_arrival) + "   " + getDurationString(data?.driverTime!!)
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

    fun getDurationString(seconds: Int): String {

        var hours = seconds / 3600
        var minutes = (seconds % 3600) / 60

        return twoDigitString(hours) + " : " + twoDigitString(minutes)
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
                tv_order_id.text = getString(R.string.order_hash).plus(orderItemModel?.id)
                tv_rest_name.text =
                    orderItemModel?.restaurantName
                if (!orderItemModel?.driverImage.isNullOrEmpty()) {
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

                if (!orderItemModel?.driverName.isNullOrEmpty()) {
                    tv_driver_name.text = orderItemModel?.driverName
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
                view?.findNavController()?.popBackStack()
                view?.findNavController()?.popBackStack()
                view?.findNavController()?.popBackStack()
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

        stop = true;
    }

    private fun updateStatus(orderStatus: Int?) {
        var checkIcon = ContextCompat.getDrawable(context!!, R.drawable.ic_checked)
        var roundIcon = ContextCompat.getDrawable(context!!, R.drawable.ic_circular_shape)
        var roundIconGrey = ContextCompat.getDrawable(context!!, R.drawable.ic_circular_shape_grey)

        bt_cancel.visibility = INVISIBLE


        when (orderStatus) {
            0 -> {
                //    0 -> "Waiting for confirmation"
                img_1.setImageDrawable(roundIconGrey)
                img_2.setImageDrawable(roundIconGrey)
                img_3.setImageDrawable(roundIconGrey)
                img_4.setImageDrawable(roundIconGrey)

//                img_1.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))
//                img_2.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))
//                img_3.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))
//                img_4.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))

                bt_cancel.visibility = VISIBLE
                tv_1.text = getString(R.string.waiting_for_confirmation)
                tv_2.text = getString(R.string.being_prepared)
                tv_3.text = getString(R.string.out_for_delivey)
                tv_4.text = getString(R.string.delivered)
            }
            2, 3 -> {
//    2 -> "Order Accepted and being prepared"

                img_1.setImageDrawable(checkIcon)
                img_1.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))
                img_2.setImageDrawable(roundIcon)
                img_3.setImageDrawable(roundIconGrey)
                img_4.setImageDrawable(roundIconGrey)
                view1.setBackgroundColor(ContextCompat.getColor(context!!, R.color.green))

                tv_1.text = getString(R.string.order_accepted)
                tv_2.text = getString(R.string.being_prepared)
                tv_3.text = getString(R.string.out_for_delivey)
                tv_4.text = getString(R.string.delivered)
            }

            8 -> {
                //    0 -> "Waiting for confirmation"
//    8 -> "Rejected by Restaurant"

//                TODO rejected
//                confirmingOrder.text = "Order confirmed"
//                txtPreparing.text = "Preparing Order"
//                waitingDone.visibility = VISIBLE
            }


            7 -> {

//    7 -> "Order ready to dispatch"
                img_1.setImageDrawable(checkIcon)
                img_2.setImageDrawable(checkIcon)
                view1.setBackgroundColor(ContextCompat.getColor(context!!, R.color.green))
                view2.setBackgroundColor(ContextCompat.getColor(context!!, R.color.green))
                img_2.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))

                img_3.setImageDrawable(roundIcon)
                img_4.setImageDrawable(roundIconGrey)
                tv_1.text = getString(R.string.order_accepted)
                tv_2.text = getString(R.string.order_prepared)
                tv_3.text = getString(R.string.ready_to_diapatch)
                tv_4.text = getString(R.string.delivered)
            }

            4 -> {

//    4 -> "Order picked up by driver,order is now its way to you"
                img_call.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake))

                img_1.setImageDrawable(checkIcon)
                img_2.setImageDrawable(checkIcon)
                img_3.setImageDrawable(checkIcon)
                img_4.setImageDrawable(roundIcon)
                view1.setBackgroundColor(ContextCompat.getColor(context!!, R.color.green))
                view2.setBackgroundColor(ContextCompat.getColor(context!!, R.color.green))
                view3.setBackgroundColor(ContextCompat.getColor(context!!, R.color.green))

                img_3.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))

                img_4.setImageDrawable(roundIcon)
                tv_1.text = getString(R.string.order_accepted)
                tv_2.text = getString(R.string.order_prepared)
                tv_3.text = getString(R.string.out_for_delivey)
                tv_4.text = getString(R.string.delivered)

//                confirmingOrder.text = "Order confirmed"
//                txtPreparing.text = "Order Prepared"
//                tv_3.text = "Out for delivery"
//                waitingDone.visibility = VISIBLE
//                preparingDone.visibility = VISIBLE
//                outForDeliveryDone.visibility = VISIBLE
            }
            5 -> {

//    5 -> "Order completed Delivered by " + orderModel.driverName

                img_1.setImageDrawable(checkIcon)
                img_2.setImageDrawable(checkIcon)
                img_3.setImageDrawable(checkIcon)
                img_4.setImageDrawable(checkIcon)
                img_4.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))

                tv_1.text = getString(R.string.order_accepted)
                tv_2.text = getString(R.string.order_prepared)
                tv_3.text = getString(R.string.out_for_delivey)
                tv_4.text = getString(R.string.delivered)


                rateDriverAndSaveOrder()


//                val bundle =
//                    bundleOf(
//                        AppConstants.ORDER_ITEM_MODEL to viewModel.orderItemRes.value,
//                        AppConstants.IS_DRIVER to true
//                    )
//                view?.findNavController()
//                    ?.navigate(R.id.action_orderDetailsFragment_to_rateDriverFragment, bundle)
            }

            else -> getString(R.string.waiting_for_confirmation)
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

    private fun fixMapScrolling() {
        transparentImage.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    parentView.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    parentView.requestDisallowInterceptTouchEvent(true)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    parentView.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> {
                    true
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_details_fragment, container, false)
//        binding = DataBindingUtil.inflate(
//            inflater,
//            R.layout.order_details_fragment,
//            container,
//            false
//        ) as OrderDetailsFragmentBinding
//        binding?.orderViewModel = viewModel
//        return binding?.root

        fixMapScrolling()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (childFragmentManager.findFragmentById(R.id.mMap) is SupportMapFragment) {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.mMap) as SupportMapFragment

            mapFragment.getMapAsync(this)
        }

        bt_cancel.setOnClickListener { viewModel.cancelOrder() }

        rel_card.startAnimation(AnimationUtils.loadAnimation(context, R.anim.bounce))

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
    }

    override fun onResume() {
        super.onResume()
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1!!.getStringExtra(ORDER_ID).equals(viewModel.orderId.value))
                    updateStatus(p1!!.getStringExtra(NOTIFICATION_TYPE).toInt())
            }
        }
        activity!!.registerReceiver(receiver, IntentFilter("com.rvtechnologies.grigora"))
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.VISIBLE
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

    fun rateDriverAndSaveOrder() {
        var rateDriverDialog = RateDriverDialogFragment(orderItemModel!!, this)
        rateDriverDialog.isCancelable = false
        rateDriverDialog.show(this.childFragmentManager, "")
    }

    override fun onDriverRateSubmit(rating: Float, orderItemModel: OrderItemModel) {
        viewModel.rateDriver(orderItemModel?.driverId!!, rating.toString())
        orderItemModel.is_driver_rated = "1"
        CommonUtils.savePrefs(context, PrefConstants.ORDER_TO_RATE, Gson().toJson(orderItemModel))
    }

    override fun onDriverRateCancel(orderItemModel: OrderItemModel) {
        CommonUtils.savePrefs(context, PrefConstants.ORDER_TO_RATE, Gson().toJson(orderItemModel))
    }

    override fun onCurveDrawn(curve: Curve?) {
    }
}
