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
import com.rvtechnologies.grigora.databinding.CartFragmentBinding
import com.rvtechnologies.grigora.databinding.GroupCartFragmentBinding
import com.rvtechnologies.grigora.model.GroupCartModel
import com.rvtechnologies.grigora.model.models.*
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.PaymentActivity
import com.rvtechnologies.grigora.view.ui.cart.adapter.AlsoOrderedCartAdapter
import com.rvtechnologies.grigora.view.ui.orders.PaymentOptionsDialog
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicks

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_login.view.*
import kotlinx.android.synthetic.main.group_cart_fragment.*
import kotlinx.android.synthetic.main.group_cart_fragment.parentView
import kotlinx.android.synthetic.main.group_cart_fragment.tv_group_order_title
import kotlinx.android.synthetic.main.group_cart_fragment.tv_order_limit
import kotlinx.android.synthetic.main.restaurant_detail_group_fragment.*
import java.util.*
import kotlin.collections.ArrayList

class GroupCartFragment : Fragment(), IRecyclerItemClick, OnMapReadyCallback, QuantityClicks,
    OnItemClickListener {

    private var mMap: GoogleMap? = null
    var placeClicked = false
    var discount: String = ""
    var restId: String = ""
    var cart_type = "1"

    private lateinit var viewModel: GroupCartViewModel
    private var cartFragmentBinding: GroupCartFragmentBinding? = null
    private val cartItemList = ArrayList<GroupCartType>()
    private val addMoreList = ArrayList<MenuItemModel>()
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
        viewModel = ViewModelProviders.of(this).get(GroupCartViewModel::class.java)
        viewModel.cartId.value = arguments?.getString(AppConstants.CART_ID)!!.toString()
        viewModel?.token?.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
        viewModel?.deliveryAddress?.value = CommonUtils.getPrefValue(context, PrefConstants.ADDRESS)
        viewModel?.deliveryLat?.value = CommonUtils.getPrefValue(context, PrefConstants.LATITUDE)
        viewModel?.deliveryLong?.value = CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE)
        viewModel?.paymentMode?.value = "1"

        viewModel?.responseCart?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    val cartDataModel = response.data as GroupCartModel
                    cart_type = cartDataModel.cart_type.toString()
                    restId = cartDataModel.restaurant_id.toString()
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

                    var cartTotal = cartSubTotal + cartDataModel.delivery_fee?.toDouble()!!

                    cartDataModel.cartSubTotal = cartSubTotal.toString()
                    cartDataModel.cartTotal = cartTotal.toString()
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

                    viewModel?.cartData?.value?.afterPromo = String.format(
                        "%.2f",
                        (viewModel?.cartData?.value?.cartSubTotal?.toDouble()!!)
                    ).toDouble()

                    viewModel?.cartData?.value?.beforePromo = String.format(
                        "%.2f",
                        (viewModel?.cartData?.value?.cartSubTotal?.toDouble()!!)
                    ).toDouble()


                    tv_estimated.text = getString(
                        R.string.deliver_in,
                        (cartDataModel.estimated_delivery_time!!.toInt() + cartDataModel.estimated_preparing_time!!.toInt()).toString()
                    )

//                    tv_restname.text = cartDataModel.restaurantName
                    tv_total.text = cartDataModel.cartTotal

                    if (!cartDataModel.add_more_items.isNullOrEmpty()) {

                    }


                    if (cartDataModel?.user_id.toString() == CommonUtils.getPrefValue(
                            context!!,
                            PrefConstants.ID
                        )
                    ) {
                        tv_change.visibility = VISIBLE
                        tv_change_payment.visibility = VISIBLE
                        offers.visibility = VISIBLE
                        button5.visibility = VISIBLE
                        tv_group_order_title.text =
                            "${getString(R.string.group_order_by)} ${cartDataModel?.cart_details[0].name}"
                        tv_order_limit.text =
                            "₦ ${cartDataModel?.max_per_person} ${getString(R.string.per_person_limit)}"
                    } else {
                        tv_change.visibility = GONE
                        tv_change_payment.visibility = GONE
                        offers.visibility = GONE
                        button5.visibility = GONE
                        tv_group_order_title.text =
                            "${cartDataModel?.cart_details[0].name}'s ${getString(R.string.group_order)}"
                        tv_order_limit.text =
                            "₦ ${cartDataModel?.max_per_person} ${getString(R.string.per_person_limit)}"
                    }

                    setPromo()

                } else {
                    empty?.visibility = View.VISIBLE
                    cartView?.visibility = View.GONE
                }
            } else {
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
                } else {
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
                } else {
                    CommonUtils.showMessage(parentView, response.toString())
                }
        })

        viewModel?.offerModel?.observe(this, Observer { offerModel ->
            //viewModel?.offerModel?.value = offerModel
            cartFragmentBinding?.cartViewModel = viewModel
            setPromo()
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
            } else {
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
        if (viewModel?.offerModel?.value != null) {
            discount = String.format(
                "%.2f",
                (viewModel?.cartData?.value?.cartSubTotal?.toDouble()!! * viewModel?.offerModel?.value!!.percentage!!.toDouble()!! / 100)
            )
            viewModel?.cartData?.value?.total_price = String.format(
                "%.2f",
                (viewModel?.cartData?.value?.cartTotal?.toDouble()!! - discount.toDouble())
            )

            viewModel?.cartData?.value?.beforePromo =
                String.format("%.2f", (viewModel?.cartData?.value?.cartSubTotal?.toDouble()!!))
                    .toDouble()

            viewModel?.cartData?.value?.afterPromo = String.format(
                "%.2f",
                (viewModel?.cartData?.value?.cartSubTotal?.toDouble()!! - discount.toDouble())
            ).toDouble()

            tv_promotion.text = discount
            tv_total.text = viewModel?.cartData?.value?.total_price
        } else {
            CommonUtils.showMessage(view, getString(R.string.no_promo_selected))
        }
    }

    fun paymentOptionsDialog() {
        placeClicked = true
        if (viewModel!!.paymentMode.value.toString().equals("1") || viewModel!!.paymentMode.value.toString().equals(
                "3"
            )
        ) {
            viewModel!!.placeOrderNow(cart_type)
        } else {
            startActivityForResult(
                Intent(
                    activity,
                    PaymentActivity::class.java
                ), 400
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
                viewModel?.reference?.value = result
                cartFragmentBinding?.cartViewModel = viewModel
                viewModel?.placeOrderNow(cart_type)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // the user canceled
        } else {
            // handle errors here, an exception may be available in

        }
    }

    fun changeLocation() {
        val builder = AlertDialog.Builder(activity!!)
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
//        if (position == -1) {
//            if (addMoreList[position2].itemCategories?.size!! > 0) {
//
//            } else {
//
//            }
//        } else {
//            viewModel?.updateCartQty(
//                viewModel?.token?.value!!,
//                cartItemList.get(position).id!!,
//                cartItemList.get(position).cartId!!,
//                "1"
//            )
//        }
    }

    override fun minus(position: Int, position2: Int) {
//        if (position == -1) {
//
//        } else {
//            viewModel?.updateCartQty(
//                viewModel?.token?.value!!,
//                cartItemList.get(position).id!!,
//                cartItemList.get(position).cartId!!,
//                "-1"
//            )
//        }
    }

    private fun setPromo() {
        viewModel?.promoId?.value = viewModel?.offerModel?.value!!.percentage!!.toString()

        tv_dis.text = viewModel?.offerModel?.value!!.code
        tv_promo.text = getString(R.string.promo_applied)
        tv_promo.setTextColor(ContextCompat.getColor(context!!, R.color.green))
        img_arrow.visibility = View.GONE
        tv_remove.visibility = View.VISIBLE

        applyPromo()
    }

    fun removePromo() {
        viewModel?.promoId?.value = "0"
        tv_dis.text = getString(R.string.apply_promocode)
        tv_promo.text = getString(R.string.no_promo_selected)
        tv_promo.setTextColor(ContextCompat.getColor(context!!, R.color.textGrey))
        img_arrow.visibility = View.VISIBLE
        tv_remove.visibility = View.GONE

        if (viewModel?.offerModel?.value != null) {
            discount = "0"

            viewModel?.cartData?.value?.total_price = String.format(
                "%.2f",
                (viewModel?.cartData?.value?.cartTotal?.toDouble()!! - discount.toDouble())
            )

            viewModel?.cartData?.value?.beforePromo =
                String.format("%.2f", (viewModel?.cartData?.value?.cartSubTotal?.toDouble()!!))
                    .toDouble()

            viewModel?.cartData?.value?.afterPromo = String.format(
                "%.2f",
                (viewModel?.cartData?.value?.cartSubTotal?.toDouble()!! - discount.toDouble())
            ).toDouble()

            tv_promotion.text = discount
            tv_total.text = viewModel?.cartData?.value?.total_price
        } else {
            CommonUtils.showMessage(view, getString(R.string.no_promo_selected))
        }

    }

    override fun onItemClick(item: Any) {
        if (item is Int) {
            if (item != 0) {
                viewModel?.paymentMode?.value = item.toString()
            }
        }
    }


}
