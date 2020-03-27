package com.rvtechnologies.grigora.view.ui.groupCart

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.GroupCartFragmentBinding
import com.rvtechnologies.grigora.model.GroupCartModel
import com.rvtechnologies.grigora.model.models.*
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.payment.PaymentActivity
import com.rvtechnologies.grigora.view.ui.cart.adapter.AlsoOrderedCartAdapter
import com.rvtechnologies.grigora.view.ui.orders.PaymentOptionsDialog
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicks

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.group_cart_fragment.*
import kotlinx.android.synthetic.main.group_cart_fragment.parentView
import kotlinx.android.synthetic.main.group_cart_fragment.tv_group_order_title
import kotlinx.android.synthetic.main.group_cart_fragment.tv_order_limit
import kotlin.collections.ArrayList

class GroupCartFragment : Fragment(), IRecyclerItemClick, OnMapReadyCallback, QuantityClicks,
    OnItemClickListener {

    private var mMap: GoogleMap? = null
    var placeClicked = false
    var dialogShown = false

    var discount: String = ""
    var restId: String = ""
    var cart_type = "1"
    lateinit var cartDataModel: GroupCartModel

    private lateinit var viewModel: GroupCartViewModel
    private var cartFragmentBinding: GroupCartFragmentBinding? = null
    private val cartItemList = ArrayList<GroupCartType>()
    private val addMoreList = ArrayList<MenuItemModel>()
    private var isPickup = false
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap?.setMinZoomPreference(12f)
        val deliveryLocation = LatLng(
            viewModel?.deliveryLat?.value?.toDouble()!!,
            viewModel?.deliveryLong?.value?.toDouble()!!
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            activity!!.let { ViewModelProviders.of(it).get(GroupCartViewModel::class.java) }

        viewModel.cartId.value = arguments?.getString(AppConstants.CART_ID)!!.toString()
        viewModel?.token?.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
        viewModel?.deliveryAddress?.value = CommonUtils.getPrefValue(context, PrefConstants.ADDRESS)
        viewModel?.deliveryLat?.value = CommonUtils.getPrefValue(context, PrefConstants.LATITUDE)
        viewModel?.deliveryLong?.value = CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE)
        viewModel?.paymentMode?.value = "1"

        viewModel?.responseCart?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    cartDataModel = response.data as GroupCartModel
                    viewModel.cartData.value = cartDataModel
                    cart_type = cartDataModel.cart_type
                    restId = cartDataModel.restaurant_id.toString()
                    viewModel.getOffers(restId)

//                    handleTime()
                    if (cart_type == "1") {
                        isPickup = false
                        tv_delivery.callOnClick()
                    } else {
                        tv_pickup.callOnClick()
                        isPickup = true
                    }

                    addMoreList.clear()
                    if (cartDataModel.add_more_items?.size != 0) {
                        addMoreList.addAll(cartDataModel.add_more_items)
                    }

                    cartFragmentBinding?.cartViewModel = viewModel

                    if ((response.data as GroupCartModel).cart_details != null && (response.data as GroupCartModel).cart_details!!.isNotEmpty()) {
                        cartItemList.clear()
                        viewModel.cartData.value = cartDataModel

                        var list = ArrayList<GroupCartType>()

                        for (item in cartDataModel.cart_details) {
                            list.add(item)
                            for (data in item.cart) {
                                list.add(data)
                            }
                        }
                        cartItemList.addAll(list)
                        rvOrderItems.adapter = GroupCartAdapter(
                            cartDataModel.user_id.toString(),
                            cartItemList,
                            this,
                            this
                        )

                        rvOrderItems?.adapter?.notifyDataSetChanged()
                        empty?.visibility = View.GONE
                        cartView?.visibility = View.VISIBLE
                    } else {
                        empty?.visibility = View.VISIBLE
                        cartView?.visibility = View.GONE
                    }

//                    tv_restname.text = cartDataModel.restaurantName
                    tv_total.text = cartDataModel.cartTotal

                    if (cartDataModel?.user_id.toString() == CommonUtils.getPrefValue(
                            context!!,
                            PrefConstants.ID
                        )
                    ) {
                        li_t.visibility = View.VISIBLE
                        tv_change.visibility = VISIBLE
                        tv_change_payment.visibility = VISIBLE
                        offers.visibility = VISIBLE
                        button5.visibility = VISIBLE
                        tv_group_order_title.text =
                            "${getString(R.string.group_order_by)} ${cartDataModel?.cart_details[0].name}"
                        tv_order_limit.text =
                            "₦ ${cartDataModel?.max_per_person} ${getString(R.string.per_person_limit)}"
                    } else {
                        li_t.visibility = View.GONE
                        tv_change.visibility = GONE
                        tv_change_payment.visibility = GONE
                        offers.visibility = GONE
                        button5.visibility = GONE
                        tv_group_order_title.text =
                            "${cartDataModel?.cart_details[0].name}'s ${getString(R.string.group_order)}"
                        tv_order_limit.text =
                            "₦ ${cartDataModel?.max_per_person} ${getString(R.string.per_person_limit)}"
                    }

                    if (viewModel.offerModel.value != null)
                        setPromo()

                    setPrices()

                } else {
                    empty?.visibility = View.VISIBLE
                    cartView?.visibility = View.GONE
                }
            } else if (response != null) {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel?.responseClearCart?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    view?.findNavController()
                        ?.navigate(
                            R.id.action_groupCartFragment_to_dashboardFragment
                        )
                } else if (response != null) {
                }
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel?.responsePlaceOrder?.observe(this, Observer { response ->
            if (placeClicked)
                if (response is CommonResponseModel<*>) {
                    CommonUtils.showMessage(parentView, response.message!!)
                    if (response.status!!) {
                        if (response.data is PlaceOrderModel) {
                            val bundle =
                                bundleOf(AppConstants.ORDER_ID to (response.data as PlaceOrderModel).orderDetails?.orderId)
                            view?.findNavController()
                                ?.navigate(
                                    R.id.action_groupCartFragment_to_orderDetailsFragment,
                                    bundle
                                )
                            viewModel?.viewGroupCart(
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
                        }
                    }
                } else if (response != null) {
                    CommonUtils.showMessage(parentView, response.toString())
                }
        })

        viewModel?.offerModel?.observe(this, Observer { offerModel ->
            //viewModel?.offerModel?.value = offerModel
            if (offerModel != null) {
                cartFragmentBinding?.cartViewModel = viewModel
                setPromo()
            }
        })
        viewModel.addCartRes?.observe(this, Observer { response ->
            if (response != null)
                viewModel.viewGroupCart(
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

        viewModel?.isLoading?.observe(this, Observer { isLoading ->
            if (isLoading) {
                context?.let { it1 -> CommonUtils.showLoader(it1, getString(R.string.loading)) }
            } else {
                CommonUtils.hideLoader()
            }
        })

        viewModel?.clientToken?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
//                    val dropInRequest = DropInRequest()
//                        .clientToken(response.data.toString())
//                    startActivityForResult(dropInRequest.getIntent(context), 100)
                } else {
                    empty.visibility = View.VISIBLE
                    cartView.visibility = View.GONE
                }
            } else if (response != null) {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel?.reference?.value = ""

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cartFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.group_cart_fragment, container, false)
        cartFragmentBinding?.cartViewModel = viewModel
        cartFragmentBinding?.cartFragment = this
        return cartFragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        if (cartItemList.size == 0 && viewModel?.cartData?.value != null) {
            cartItemList.addAll((viewModel?.cartData?.value?.cart_details as Collection<CartDetail>))
            rvOrderItems?.adapter?.notifyDataSetChanged()
            empty?.visibility = View.GONE
            cartView?.visibility = View.VISIBLE
        }

        rv_people_also_ordered.adapter = AlsoOrderedCartAdapter(addMoreList, this, this, -1)
        manageSwitch()

    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle("")
            (activity as MainActivity).lockDrawer(true)
            (activity as MainActivity).img_right.visibility = View.VISIBLE
            (activity as MainActivity).img_right.setImageResource(R.drawable.ic_delete)
            (activity as MainActivity).img_right.setOnClickListener {
                viewModel.clearCart()
            }

        }

        val token = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

        viewModel?.viewGroupCart(
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
                R.id.action_groupCartFragment_to_restaurantDetailParent, bundle
            )

    }

    fun showPaymentOptionsDialog() {
        var optionsDialog = PaymentOptionsDialog(this)
        optionsDialog.show(this.childFragmentManager, "")
    }


    private fun toLogin() {
        view?.findNavController()
            ?.navigate(
                R.id.action_groupCartFragment_to_loginFragment2
            )
    }

    fun showOffers() {
        view?.findNavController()
            ?.navigate(
                R.id.action_groupCartFragment_to_offerFragment
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

    fun paymentOptionsDialog() {
        placeClicked = true
        if (viewModel!!.paymentMode.value.toString()
                .equals("1") || viewModel!!.paymentMode.value.toString().equals(
                "3"
            )
        ) {
            viewModel!!.placeOrderNow(cart_type)
        } else {
            startActivityForResult(
                Intent(
                    activity,
                    PaymentActivity::class.java
                ).putExtra("amount", viewModel?.cartData?.value?.total_price!!.toDouble().toInt()),
                400
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 400) {
            val result = data?.getStringExtra("reference")
            if (result != null) {
                viewModel.reference?.value = result
                cartFragmentBinding?.cartViewModel = viewModel

                viewModel.placeOrderNow(cart_type)

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // the user canceled
        } else {
            // handle errors here, an exception may be available in

        }
    }

    fun changeLocation() {
        val builder = AlertDialog.Builder(activity!!,R.style.TimePickerTheme)
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
                    R.id.action_groupCartFragment_to_selectLocationFragment
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

        } else {
            var data = cartItemList[position] as CartDetail
            viewModel.addItemToCart(
                restId, data.itemId.toString(), data.price.toString(), "1"
            )
        }
    }

    override fun minus(position: Int, position2: Int) {
        if (position == -1) {

        } else {
            var data = cartItemList[position] as CartDetail
            viewModel.addItemToCart(
                restId, data.itemId.toString(), data.price.toString(), "-1"
            )
        }
    }


    private fun setPromo() {
        viewModel.promoId?.value = viewModel.offerModel?.value!!.percentage!!.toString()

        tv_dis.text = viewModel.offerModel?.value!!.code
        tv_promo.text = getString(R.string.promo_applied)
        tv_promo.setTextColor(ContextCompat.getColor(context!!, R.color.green))
        img_arrow.visibility = GONE
        tv_remove.visibility = VISIBLE

        applyPromo()
    }

    fun removePromo() {
        viewModel.promoId?.value = "0"
        tv_dis.text = getString(R.string.apply_promocode)
        tv_promo.text = getString(R.string.no_promo_selected)
        tv_promo.setTextColor(ContextCompat.getColor(context!!, R.color.textGrey))
        img_arrow.visibility = VISIBLE
        tv_remove.visibility = GONE

        if (viewModel.offerModel?.value != null) {
            viewModel.cartData?.value?.discount = "0"



            tv_promotion.text = viewModel.cartData?.value?.discount

        } else {
            CommonUtils.showMessage(view, getString(R.string.no_promo_selected))
        }
        setPrices()
    }

    override fun onItemClick(item: Any) {
        if (item is Int) {
            if (item != 0) {
                viewModel?.paymentMode?.value = item.toString()
            }
        }
    }

    fun invite() {

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            viewModel.cartData.value?.share_link
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    //    SHOW CURRENTLY CLOSED, TRY LATER
    private fun handleTime() {
        if (cartDataModel.fullTime == "0") {
            if (!CommonUtils.isRestaurantOpen(
                    cartDataModel.openingTime!!,
                    cartDataModel.closingTime!!
                ) && !dialogShown
            ) {
                dialogShown = true

                CommonUtils.showMessage(parentView, getString(R.string.restaurant_not_available))
            }
        }

        if (((CommonUtils.isRestaurantOpen(
                cartDataModel.openingTime,
                cartDataModel.closingTime
            ) || cartDataModel.fullTime == "1") && cartDataModel.busy_status == "1"
                    ) &&
            !dialogShown
        ) {
            dialogShown = true
            CommonUtils.showMessage(parentView, getString(R.string.restaurant_not_available))

        }
    }

    private fun manageSwitch() {
        tv_delivery.setOnClickListener {
            cart_type="1"

            tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_delivery.setBackgroundResource(R.drawable.delivery_sel)
            tv_pickup.setBackgroundResource(R.drawable.pickup_de_sel)

            isPickup = false
            setPrices()

            if (CommonUtils.isDarkMode()) {
                tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            } else
                tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

            viewModel.updateType(
                restId,
                "1",
                CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
            )

        }

        tv_pickup.setOnClickListener {
            cart_type="2"
            tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_pickup.setBackgroundResource(R.drawable.pickup_sel)

            tv_delivery.setBackgroundResource(R.drawable.delivery_de_sel)

            isPickup = true
            setPrices()

            if (CommonUtils.isDarkMode()) {
                tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            } else
                tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

            viewModel.updateType(
                restId,
                "2",
                CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
            )
        }
    }

    private fun setPrices() {
        if (viewModel.cartData?.value?.discount.isNullOrEmpty()) {
            viewModel.cartData?.value?.discount = "0.0"
        }
        tv_promotion.text = viewModel.cartData?.value?.discount

        viewModel.deliveryPrice.value =
            if (isPickup) "0.0" else CommonUtils.getRoundedOff(getDeliveryPrice())


        var cartSubTotal = 0.0
        for (cartDetail in cartDataModel.cart_details!!) {
            for (innerDetail in cartDetail.cart) {
                var price = innerDetail?.price?.toDouble()!!

                var addOnPrice = 0.0

                if (innerDetail.item_choices != null && innerDetail.item_choices?.isNotEmpty()!!) {

                    for (item in innerDetail.item_choices!!) {
                        for (innerItem in item.itemSubCategory!!) {
                            addOnPrice += innerItem?.addOnPrice!!.toDouble()
                        }
                    }
                }
                cartSubTotal += ((price + addOnPrice) * innerDetail?.quantity!!.toDouble())
            }
        }

        viewModel.cartData?.value!!.cartTotal =
            (cartSubTotal + viewModel.deliveryPrice.value!!.toDouble()).toString()
        cartDataModel.cartSubTotal = cartSubTotal.toString()
        viewModel.cartData?.value?.cartSubTotal = cartSubTotal.toString()


        viewModel.cartData?.value?.totalPrice =
            CommonUtils.getRoundedOff(viewModel.cartData?.value?.cartTotal?.toDouble()!! - viewModel.cartData?.value?.discount!!.toDouble())

        viewModel.cartData?.value?.beforePromo =
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
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy(activity as MainActivity)
    }


}
