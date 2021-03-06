package com.rvtechnologies.grigora.view.ui.cart

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.CartFragmentBinding
import com.rvtechnologies.grigora.model.models.*
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.payment.PaymentActivity
import com.rvtechnologies.grigora.view.ui.cart.adapter.AlsoOrderedCartAdapter
import com.rvtechnologies.grigora.view.ui.cart.adapter.CartAdapter
import com.rvtechnologies.grigora.view.ui.orders.PaymentOptionsDialog
import com.rvtechnologies.grigora.view.ui.restaurant_detail.MealDetailSheet
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicks
import com.rvtechnologies.grigora.view_model.CartNdOfferViewModel
import com.rvtechnologies.grigora.view_model.CartSharedViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.cart_fragment.*
import kotlin.collections.ArrayList

class CartFragment : Fragment(), IRecyclerItemClick, OnMapReadyCallback, QuantityClicks,
    OnItemClickListener ,MealDetailSheet.Refresh{
    private var mMap: GoogleMap? = null
    var dialogShown = false
    var placeClicked = false
    var restId: String = ""
    var cart_type = "1"
    var restaurantId = ""
    lateinit var cartDataModel: CartDataModel
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.setMinZoomPreference(12f)
        val deliveryLocation = LatLng(
            viewModel.deliveryLat?.value?.toDouble()!!,
            viewModel.deliveryLong?.value?.toDouble()!!
        )
        val marker = MarkerOptions().position(deliveryLocation)
        mMap?.addMarker(marker)
        mMap?.uiSettings?.setAllGesturesEnabled(false)
        mMap?.uiSettings?.isZoomControlsEnabled = false
        mMap?.uiSettings?.isZoomGesturesEnabled = false
        mMap?.uiSettings?.isScrollGesturesEnabled = false
        mMap?.uiSettings?.isMyLocationButtonEnabled = false
        mMap?.uiSettings?.isZoomControlsEnabled = false
        mMap?.uiSettings?.isZoomGesturesEnabled = false
        mMap?.uiSettings?.isScrollGesturesEnabled = false
        mMap?.uiSettings?.isMyLocationButtonEnabled = false
        mMap?.uiSettings?.isMapToolbarEnabled = false
        mMap?.uiSettings?.isCompassEnabled = false
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(deliveryLocation))
    }

    private lateinit var viewModel: CartNdOfferViewModel
    lateinit var cartSharedViewModel: CartSharedViewModel

    private var cartFragmentBinding: CartFragmentBinding? = null
    private val cartItemList = ArrayList<CartDetail>()
    private val addMoreList = ArrayList<RestaurantDetailNewModel.MealItem>()
    private var isPickup = false
    private var load = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        viewModel = ViewModelProviders.of(this).get(CartNdOfferViewModel::class.java)

        viewModel =
            activity!!.let { ViewModelProviders.of(it).get(CartNdOfferViewModel::class.java) }
        cartSharedViewModel =
            activity!!.let { ViewModelProviders.of(it).get(CartSharedViewModel::class.java) }

        viewModel.token?.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
        viewModel.deliveryAddress?.value = CommonUtils.getPrefValue(context, PrefConstants.ADDRESS)
        viewModel.deliveryLat?.value = CommonUtils.getPrefValue(context, PrefConstants.LATITUDE)
        viewModel.deliveryLong?.value = CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE)
        viewModel.paymentMode?.value = "3"

        viewModel.isLoading?.observe(this, Observer { isLoading ->
            if (isLoading) {
                context?.let { it1 -> CommonUtils.showLoader(it1, getString(R.string.loading)) }
            } else {
                CommonUtils.hideLoader()
            }
        })

        viewModel.responseCart?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    cartDataModel = response.data as CartDataModel

                    viewModel.cartData?.value = response.data as CartDataModel
                    restaurantId = cartDataModel.restaurantId!!
                    viewModel.getOffers(restaurantId)

                    handleTime()
                    cart_type = cartDataModel.cart_type.toString()

                    if (cartDataModel.pickup == "0") {
                        li_enabled.visibility = View.GONE
                        isPickup = false
                        tv_delivery.callOnClick()
                    } else {
                        if (cart_type == "1") {
                            isPickup = false
                            tv_delivery.callOnClick()
                        } else {
                            tv_pickup.callOnClick()
                            isPickup = true
                        }
                    }
                    restId = cartDataModel.restaurantId.toString()



                    addMoreList.clear()
                    if (cartDataModel.add_more_items?.size != 0) {
                        addMoreList.addAll(cartDataModel.add_more_items)
                    }
                    if (addMoreList.size > 0) {
                        li_also_ordered.visibility = View.VISIBLE
                        rv_people_also_ordered.adapter =
                            AlsoOrderedCartAdapter(addMoreList, this, this, -1)
                    } else
                        li_also_ordered.visibility = GONE

                    cartFragmentBinding?.cartViewModel = viewModel

                    if ((response.data as CartDataModel).cartDetails != null && (response.data as CartDataModel).cartDetails!!.isNotEmpty()) {
                        cartItemList.clear()
                        cartItemList.addAll((response.data as CartDataModel).cartDetails as Collection<CartDetail>)
                        rvOrderItems?.adapter?.notifyDataSetChanged()
                        empty?.visibility = GONE
                        cartView?.visibility = VISIBLE
                    } else {
                        empty?.visibility = VISIBLE
                        cartView?.visibility = GONE
                    }


                    if (viewModel.offerModel.value != null)
                        setPromo()

                    setPrices()

                } else {
                    CommonUtils.showMessage(parentView, response.message.toString())

                    AppConstants.CART_COUNT = 0
                    AppConstants.CART_RESTAURANT = ""
//                    empty?.visibility = VISIBLE
//                    cartView?.visibility = GONE
                    (activity as MainActivity).clearStack()
                    (activity as MainActivity).selectedNavigation(R.id.dashBoardFragment)
                }
            }
//            else if (response != null) {
//                CommonUtils.showMessage(parentView, response.toString())
//            }
        })

        viewModel.responseClearCart?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    view?.findNavController()
                        ?.navigate(
                            R.id.action_navigationCart_to_dashboardFragment
                        )
                }
            } else if (response != null) {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel.responsePlaceOrder?.observe(this, Observer { response ->
            if (placeClicked)
                if (response is CommonResponseModel<*>) {
                    CommonUtils.showMessage(parentView, response.message!!)
                    if (response.status!!) {
                        if (response.data is PlaceOrderModel) {
                            val bundle =
                                bundleOf(AppConstants.ORDER_ID to (response.data as PlaceOrderModel).orderDetails?.orderId)
                            view?.findNavController()
                                ?.navigate(
                                    R.id.action_navigationCart_to_orderDetailsFragment,
                                    bundle
                                )

                        }
                    }
                } else if (response != null) {
                    CommonUtils.showMessage(parentView, response.toString())
                }
        })

        viewModel.responseScheduleOrder?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                CommonUtils.showMessage(parentView, response.message!!)
                if (response.status!!) {
                    if (response.data is PlaceOrderModel) {

                        var scheduleSuccess = ScheduleSuccess(
                            this,
                            cartSharedViewModel.scheduleDate.value!!,
                            cartSharedViewModel.scheduleTime.value!!
                            , isPickup
                        )
                        scheduleSuccess.show(childFragmentManager, "")
                    }
                }
            } else if (response != null) {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel.clientToken?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
//                    val dropInRequest = DropInRequest()
//                        .clientToken(response.data.toString())
//                    startActivityForResult(dropInRequest.getIntent(context), 100)
                } else {
                    empty.visibility = VISIBLE
                    cartView.visibility = GONE
                }
            } else if (response != null) {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel.addCartRes?.observe(this, Observer { response ->
            if (response != null)
                viewModel.viewCart(
                    CommonUtils.getPrefValue(
                        context,
                        PrefConstants.TOKEN
                    ), CommonUtils.getPrefValue(
                        context,
                        PrefConstants.LATITUDE
                    ), CommonUtils.getPrefValue(
                        context,
                        PrefConstants.LONGITUDE
                    )
                )
        })

        viewModel.reference?.value = ""
        handleScheduleViewModel()

        viewModel.offerModel?.observe(this, Observer { offerModel ->
            if (offerModel != null) {
                cartFragmentBinding?.cartViewModel = viewModel
                setPromo()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cartFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.cart_fragment, container, false)
        cartFragmentBinding?.cartViewModel = viewModel
        cartFragmentBinding?.cartFragment = this
        return cartFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (cartItemList.size == 0 && viewModel.cartData?.value != null) {
            cartItemList.addAll((viewModel.cartData?.value?.cartDetails as Collection<CartDetail>))
            rvOrderItems?.adapter?.notifyDataSetChanged()
            empty?.visibility = GONE
            cartView?.visibility = VISIBLE
        }

        if (cartSharedViewModel.isScheduledOrder.value != null && cartSharedViewModel.isScheduledOrder.value!!) {
            button5.text = getString(R.string.schedule_order)
        }
        manageSwitch()

        if (cartSharedViewModel != null && cartSharedViewModel.scheduleNote.value != null)
            viewModel.preparationNote.value = cartSharedViewModel.scheduleNote.value

    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle(getString(R.string.checkout))
            (activity as MainActivity).lockDrawer(true)

            (activity as MainActivity).img_right.visibility = VISIBLE
            (activity as MainActivity).img_right.setImageResource(R.drawable.ic_delete)
            (activity as MainActivity).img_right.setOnClickListener {
                viewModel.clearCart()
            }

        }

        rvOrderItems.adapter = CartAdapter(cartItemList, this, this)

        if (load)
            viewModel.viewCart(
                CommonUtils.getPrefValue(context, PrefConstants.TOKEN), CommonUtils.getPrefValue(
                    context,
                    PrefConstants.LATITUDE
                ), CommonUtils.getPrefValue(
                    context,
                    PrefConstants.LONGITUDE
                )
            )


    }

    fun addMore() {
//        navigate to restaurant detail
        val bundle = bundleOf(
            AppConstants.RESTAURANT_ID to restId,
            AppConstants.RESTAURANT_PICKUP to "0",
            AppConstants.RESTAURANT_BOOKING to "0",
            AppConstants.RESTAURANT_SEATES to "0",
            AppConstants.RESTAURANT_CLOSING_TIME to "0",
            AppConstants.RESTAURANT_OPENING_TIME to "0",
            AppConstants.RESTAURANT_ALWAYS_OPEN to "0",
            AppConstants.FROM_PICKUP to false
        )

        view?.findNavController()
            ?.navigate(
                R.id.action_navigationCart_to_restaurantDetailParent, bundle
            )

    }

    fun showPaymentOptionsDialog() {
        var optionsDialog = PaymentOptionsDialog(this)
        optionsDialog.show(this.childFragmentManager, "")
    }

    fun showOffers() {
        view?.findNavController()
            ?.navigate(
                R.id.action_navigationCart_to_offerFragment
            )
    }

    private fun applyPromo() {
        if (viewModel.offerModel?.value != null) {
            viewModel.cartData?.value?.discount =
                CommonUtils.getRoundedOff(viewModel.cartData?.value?.cartSubTotal?.toDouble()!! * viewModel.offerModel?.value!!.percentage!!.toDouble()!! / 100)

            setPrices()
        } else {
            CommonUtils.showMessage(view, getString(R.string.no_promo_selected))
        }
    }

    fun paymentAndPlace() {
        placeClicked = true
        if (viewModel!!.paymentMode.value.toString()
                .equals("1") || viewModel!!.paymentMode.value.toString().equals(
                "3"
            )
        ) {
            if (!CommonUtils.isLogin()) {
                (activity as MainActivity).showLoginAlert()
            } else {
                if (cartSharedViewModel.isScheduledOrder.value != null && cartSharedViewModel.isScheduledOrder.value!!) {
                    var time = CommonUtils.localToUtc(
                        cartSharedViewModel.scheduleDate.value!! + " " + cartSharedViewModel.scheduleTime.value!!,
                        "yyyy-MM-dd HH:mm:ss"
                    )
                    viewModel.scheduleOrderNow(
                        cart_type,
                        time
                    )
                } else {
                    viewModel!!.placeOrderNow(cart_type)
                }
            }
        } else {
            startActivityForResult(
                Intent(
                    activity,
                    PaymentActivity::class.java
                ).putExtra("amount", viewModel?.cartData.value?.totalPrice!!.toDouble()),
                400
            )
//            var mBottomSheetDialog = BottomSheetDialog(activity!!)
//            var sheetView = activity!!.layoutInflater.inflate(
//                R.layout.dialog_payment_options,
//                null
//            )
//            mBottomSheetDialog.setContentView(sheetView)
//            mBottomSheetDialog.show()
//
//            sheetView.findViewById<LinearLayout>(R.id.li_card).setOnClickListener {
//                startActivityForResult(
//                    Intent(
//                        activity,
//                        PaymentActivity::class.java
//                    ), 400
//                )
//                mBottomSheetDialog.dismiss()
//
//            }
//            sheetView.findViewById<LinearLayout>(R.id.li_paypal).setOnClickListener { }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 400) {
            val result = data?.getStringExtra("reference")
            if (result != null) {
                viewModel.reference?.value = result
                cartFragmentBinding?.cartViewModel = viewModel

                if (!CommonUtils.isLogin()) {
                    (activity as MainActivity).showLoginAlert()
                } else {
                    load = false
                    if (cartSharedViewModel.isScheduledOrder.value != null && cartSharedViewModel.isScheduledOrder.value!!) {
                        var time = CommonUtils.localToUtc(
                            cartSharedViewModel.scheduleDate.value!! + " " + cartSharedViewModel.scheduleTime.value!!,
                            "yyyy-MM-dd HH:mm:ss"
                        )


                        viewModel.scheduleOrderNow(
                            cart_type,
                            time
                        )
                    } else {
                        viewModel.placeOrderNow(cart_type)


                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            // the user canceled
        } else {
            // handle errors here, an exception may be available in

        }
    }

    fun changeLocation() {
        val builder = AlertDialog.Builder(activity!!, R.style.TimePickerTheme)
        //set title for alert dialog
        builder.setTitle(R.string.change_title)
        //set message for alert dialog
        builder.setMessage(R.string.change_desc)
        //performing positive action
        builder.setPositiveButton(R.string.continueTxt) { dialogInterface, which ->
            dialogInterface.dismiss()
            CommonUtils.savePrefs(context, PrefConstants.LATITUDE, "")
            CommonUtils.savePrefs(context, PrefConstants.LONGITUDE, "")
            CommonUtils.savePrefs(context, PrefConstants.ADDRESS, "")

            view?.findNavController()
                ?.navigate(
                    R.id.action_navigationCart_to_selectLocationFragment
                )
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

    override fun add(position: Int, position2: Int) {
        if (position == -1) {
            if (addMoreList[position2].item_categories?.size!! > 0) {
                viewModel.addItemToCart(
                    addMoreList[position2].restaurant_id.toString(),
                    addMoreList[position2].id.toString(),
                    addMoreList[position2].price.toString(),
                    "1"
                )
            } else {
//                val bundle =
//                    bundleOf(AppConstants.MENU_ITEM_MODEL to addMoreList[position2])
//
//                view?.findNavController()
//                    ?.navigate(
//                        R.id.action_navigationCart_to_menuItemDetailsFragment, bundle
//                    )

                var sheet = MealDetailSheet(addMoreList[position2], "", this)
                sheet.show(childFragmentManager, "")
            }
        } else {
            viewModel.updateCartQty(
                viewModel.token.value!!,
                cartItemList.get(position).id!!,
                cartItemList.get(position).cartId!!,
                "1"
            )
        }
    }

    override fun minus(position: Int, position2: Int) {
        if (position == -1) {

        } else {
            viewModel.updateCartQty(
                viewModel.token.value!!,
                cartItemList.get(position).id!!,
                cartItemList.get(position).cartId!!,
                "-1"
            )
        }
    }

    private fun setPromo() {
        viewModel.promoId.value = viewModel.offerModel.value!!.id!!.toString()

        tv_dis.text = viewModel.offerModel.value!!.code
        tv_promo.text = getString(R.string.promo_applied)
        tv_promo.setTextColor(ContextCompat.getColor(context!!, R.color.green))
        img_arrow.visibility = GONE
        tv_remove.visibility = VISIBLE

        applyPromo()
    }

    fun removePromo() {
        viewModel.promoId.value = "0"
        tv_dis.text = getString(R.string.apply_promocode)
        tv_promo.text = getString(R.string.no_promo_selected)
        tv_promo.setTextColor(ContextCompat.getColor(context!!, R.color.textGrey))
        img_arrow.visibility = VISIBLE
        tv_remove.visibility = GONE

        if (viewModel.offerModel.value != null) {
            viewModel.cartData.value?.discount = "0"



            tv_promotion.text = viewModel.cartData.value?.discount

        } else {
            CommonUtils.showMessage(view, getString(R.string.no_promo_selected))
        }
        setPrices()
    }

    override fun onItemClick(item: Any) {
        if (item is Int) {
            if (item != 0) {
                viewModel.paymentMode.value = item.toString()
                if (item == 2) {
                    tv_payment_type.text = getString(R.string.credit_or_debit_card)
                }
                if (item == 3) {
                    tv_payment_type.text = getString(R.string.gora_pouch)
                }
            }
        } else if (item is String) {
            view?.findNavController()
                ?.navigate(
                    R.id.action_navigationCart_to_dashboardFragment
                )
        } else if (item is Boolean) {
            if (item) {
                scheduleOrder()
            } else {
                view?.findNavController()?.popBackStack()
            }
        } else if (item is RestaurantDetailNewModel.MealItem) {
//            val bundle =
//                bundleOf(AppConstants.MENU_ITEM_MODEL to item)


            var sheet = MealDetailSheet(item, "", this)
            sheet.show(childFragmentManager, "")

//            view?.findNavController()
//                ?.navigate(
//                    R.id.action_navigationCart_to_menuItemDetailsFragment, bundle
//                )
        }
    }

    fun scheduleOrder() {

        var bundle = bundleOf(
            AppConstants.RESTAURANT_OPENING_TIME to cartDataModel.openingTime,
            AppConstants.RESTAURANT_CLOSING_TIME to cartDataModel.closingTime,
            AppConstants.FROM_RESTAURANT_DETAIL to false,
            AppConstants.RESTAURANT_ALWAYS_OPEN to cartDataModel.fullTime
        )

        view?.findNavController()
            ?.navigate(
                R.id.action_navigationCart_to_scheduleOrder
                , bundle
            )
    }

    private fun handleScheduleViewModel() {
//        cartSharedViewModel.isScheduledOrder.value = false
        cartSharedViewModel.isScheduledOrder.observe(this, Observer { it ->
            if (it != null) {
                button5.text = getString(R.string.schedule_order)
            }
        })
    }

    private fun handleTime() {

        if (cartSharedViewModel.scheduleDate.value.isNullOrEmpty() || cartSharedViewModel.scheduleTime.value.isNullOrEmpty())
            dialogShown = false


        if (cartDataModel.fullTime == "0") {
            if (!CommonUtils.isRestaurantOpen(
                    cartDataModel.openingTime!!,
                    cartDataModel.closingTime!!
                ) && !dialogShown
            ) {
                dialogShown = true

                var scheduleOrderAlert = ScheduleAlert(this)
                scheduleOrderAlert.show(childFragmentManager, "")
            }
        }
        if (((CommonUtils.isRestaurantOpen(
                cartDataModel.openingTime!!,
                cartDataModel.closingTime!!
            ) || cartDataModel.fullTime == "1") && cartDataModel.busyStatus == "1"
                    ) &&
            !dialogShown
        ) {
            dialogShown = true
            var scheduleOrderAlert = ScheduleAlert(this)
            scheduleOrderAlert.show(childFragmentManager, "")
        }
    }

    private fun manageSwitch() {
        tv_delivery.setOnClickListener {
            if (CommonUtils.isDarkMode()) {
                tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            } else {
                tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
                tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            }
            tv_delivery.setBackgroundResource(R.drawable.delivery_sel)
            tv_pickup.setBackgroundResource(R.drawable.pickup_de_sel)




            cart_type = "1"

            isPickup = false
            setPrices()

            viewModel.updateType(
                restaurantId,
                "1",
                CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
            )

        }

        tv_pickup.setOnClickListener {
            tv_pickup.setBackgroundResource(R.drawable.pickup_sel)
            tv_delivery.setBackgroundResource(R.drawable.delivery_de_sel)

            if (CommonUtils.isDarkMode()) {
                tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            } else {
                tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            }

            cart_type = "2"
            isPickup = true
            setPrices()
            viewModel.updateType(
                restaurantId,
                "2",
                CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy(activity as MainActivity)
        cartSharedViewModel.destroy(activity as MainActivity)
    }

    private fun setPrices() {
        if (viewModel.cartData.value?.discount.isNullOrEmpty()) {
            viewModel.cartData.value?.discount = "0.0"
        }
        tv_promotion.text = viewModel.cartData.value?.discount

        viewModel.deliveryPrice.value =
            if (isPickup) "0.0" else CommonUtils.getRoundedOff(getDeliveryPrice())


        var cartSubTotal = 0.0
        for (cartDetail in cartDataModel.cartDetails!!) {
            var price = cartDetail?.price?.toDouble()!!
            var addOnPrice = 0.0
//            if (cartDetail.item_choices != null && cartDetail.item_choices?.isNotEmpty()!!) {
//                for (item in cartDetail.item_choices!!) {
//                    for (innerItem in item.itemSubCategory!!) {
//                        addOnPrice += innerItem?.addOnPrice!!
//                        if (innerItem.item_sub_sub_category!!.isNotEmpty()) {
//                            for (i in innerItem.item_sub_sub_category!!) {
//                                if (i?.add_on_price!! > 0)
//                                    addOnPrice += i.add_on_price
//                            }
//                        }
//                    }
//                }
//            }
            cartSubTotal += ((price) * Integer.parseInt(cartDetail.quantity))
        }

        viewModel.cartData.value!!.cartTotal =
            (cartSubTotal + viewModel.deliveryPrice.value!!.toDouble()).toString()
        cartDataModel.cartSubTotal = cartSubTotal.toString()
        viewModel.cartData.value?.cartSubTotal = cartSubTotal.toString()


        viewModel.cartData.value?.totalPrice =
            CommonUtils.getRoundedOff(viewModel.cartData.value?.cartTotal?.toDouble()!! - viewModel.cartData.value?.discount!!.toDouble())

        viewModel.cartData.value?.beforePromo =
            CommonUtils.getRoundedOff(viewModel.cartData?.value?.cartSubTotal?.toDouble()!!)
                .toDouble()

        viewModel.cartData?.value?.afterPromo =
            CommonUtils.getRoundedOff((viewModel.cartData?.value?.cartSubTotal?.toDouble()!! - viewModel.cartData?.value?.discount!!.toDouble()))
                .toDouble()

        tv_subtotal.text = CommonUtils.getRoundedOff(viewModel.cartData?.value?.beforePromo!!)


        tv_total.text = viewModel.cartData?.value?.totalPrice
        tv_delivery_price.text = viewModel.deliveryPrice.value


        var distance = if (!isPickup) CommonUtils.calculateDistance(
            cartDataModel?.restaurant_latitude.toDouble(),
            cartDataModel?.restaurant_longitude.toDouble(),
            CommonUtils.getPrefValue(context, PrefConstants.LATITUDE).toDouble(),
            CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE).toDouble()
        ) else 0F

        var t = cartDataModel.estimated_preparing_time!!.toInt() + (distance * 2)

        val hours: Int =
            t.toInt() / 60 //since both are ints, you get an int

        val minutes: Int = t.toInt() % 60

        if (hours > 0) {
            if (isPickup) {
                tv_msg.text = getString(R.string.will_prepare_in)
                tv_estimated.text =
                    "$hours hours and $minutes minutes"
            } else {
                tv_msg.text = getString(R.string.deliver_to_you)

                tv_estimated.text =
                    "$hours hours and $minutes minutes"
            }

        } else {
            if (isPickup) {
                tv_msg.text = getString(R.string.will_prepare_in)

                tv_estimated.text =
                    "$minutes minutes"
            } else {
                tv_msg.text = getString(R.string.deliver_to_you)

                tv_estimated.text =
                    "$minutes minutes"
            }
        }
    }

    fun getDeliveryPrice(): Double {
        return cartDataModel.delivery_fee!!.toDouble()

        var distance = CommonUtils.calculateDistance(
            cartDataModel?.restaurant_latitude.toDouble(),
            cartDataModel?.restaurant_longitude.toDouble(),
            CommonUtils.getPrefValue(context, PrefConstants.LATITUDE).toDouble(),
            CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE).toDouble()
        )

        return CommonUtils.getPrefValue(context, PrefConstants.BASE_DELIVERY_FEE)
            .toDouble() + (distance * CommonUtils.getPrefValue(
            context,
            PrefConstants.MIN_KILO_METER
        ).toDouble())
    }

    override fun refresh(refresh: Boolean) {
        viewModel.viewCart(
            CommonUtils.getPrefValue(context, PrefConstants.TOKEN), CommonUtils.getPrefValue(
                context,
                PrefConstants.LATITUDE
            ), CommonUtils.getPrefValue(
                context,
                PrefConstants.LONGITUDE
            )
        )    }


}
