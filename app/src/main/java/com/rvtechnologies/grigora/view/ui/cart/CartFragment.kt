package com.rvtechnologies.grigora.view.ui.cart

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.CartFragmentBinding
import com.rvtechnologies.grigora.model.models.*
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.PaymentActivity
import com.rvtechnologies.grigora.view.ui.cart.adapter.AlsoOrderedCartAdapter
import com.rvtechnologies.grigora.view.ui.cart.adapter.CartAdapter
import com.rvtechnologies.grigora.view.ui.orders.PaymentOptionsDialog
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicks
import com.rvtechnologies.grigora.view_model.CartNdOfferViewModel
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.SharingHelper
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import io.branch.referral.util.ShareSheetStyle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_login.view.*
import kotlinx.android.synthetic.main.cart_fragment.*
import kotlinx.android.synthetic.main.refer_and_earn_fragment.*
import java.util.*
import kotlin.collections.ArrayList

class CartFragment : Fragment(), IRecyclerItemClick, OnMapReadyCallback, QuantityClicks,
    OnItemClickListener {
    private var mMap: GoogleMap? = null
    var placeClicked = false
    var discount: String = ""
    var restId: String = ""
    var cart_type = "1"
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

    companion object {
        fun newInstance() = CartFragment()
    }

    private var viewModel: CartNdOfferViewModel? = null
    private var cartFragmentBinding: CartFragmentBinding? = null
    private val cartItemList = ArrayList<CartDetail>()
    private val addMoreList = ArrayList<MenuItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CartNdOfferViewModel::class.java)

//        if (viewModel == null)
//            viewModel = activity?.run {
//                ViewModelProviders.of(this).get(CartNdOfferViewModel::class.java)
//            } ?: throw Exception("Invalid Activity")

        viewModel?.token?.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
        viewModel?.deliveryAddress?.value = CommonUtils.getPrefValue(context, PrefConstants.ADDRESS)
        viewModel?.deliveryLat?.value = CommonUtils.getPrefValue(context, PrefConstants.LATITUDE)
        viewModel?.deliveryLong?.value = CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE)
        viewModel?.paymentMode?.value = "1"

        viewModel?.responseCart?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    val cartDataModel = response.data as CartDataModel
                    cart_type = cartDataModel.cart_type.toString()
                    restId = cartDataModel.restaurantId.toString()
                    var cartSubTotal = 0.0
                    for (cartDetail in cartDataModel.cartDetails!!) {
                        var price = cartDetail?.price?.toDouble()!!

                        var addOnPrice = 0.0

                        if (cartDetail.item_choices != null && cartDetail.item_choices?.isNotEmpty()!!) {

                            for (item in cartDetail.item_choices!!) {
                                for (innerItem in item.itemSubCategory!!) {
                                    addOnPrice += innerItem?.addOnPrice!!
                                }
                            }
                        }
                        cartSubTotal += ((price + addOnPrice) * Integer.parseInt(cartDetail?.quantity!!))
                    }

                    var cartTotal = cartSubTotal + cartDataModel.deliveryFee?.toDouble()!!

                    cartDataModel.cartSubTotal = cartSubTotal.toString()
                    cartDataModel.cartTotal = cartTotal.toString()
                    if (cartDataModel.add_more_items?.size != 0) {
                        addMoreList.addAll(cartDataModel.add_more_items)
                    }
                    viewModel?.cartData?.value = response.data as CartDataModel

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

                    setPromo()


                    if (!cartDataModel.add_more_items.isNullOrEmpty()) {

                    }


                } else {
                    empty?.visibility = VISIBLE
                    cartView?.visibility = GONE
                }
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel?.responseClearCart?.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    viewModel?.viewCart(
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
                                    R.id.action_navigationCart_to_orderDetailsFragment,
                                    bundle
                                )
                            viewModel?.viewCart(
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
                    empty.visibility = VISIBLE
                    cartView.visibility = GONE
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


        if (cartItemList.size == 0 && viewModel?.cartData?.value != null) {
            cartItemList.addAll((viewModel?.cartData?.value?.cartDetails as Collection<CartDetail>))
            rvOrderItems?.adapter?.notifyDataSetChanged()
            empty?.visibility = GONE
            cartView?.visibility = VISIBLE
        }

        rv_people_also_ordered.adapter = AlsoOrderedCartAdapter(addMoreList, this, this, -1)

    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle("")
            (activity as MainActivity).lockDrawer(true)
        }

        val token = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
        if (token.isBlank()) {
            empty?.visibility = VISIBLE
            cartView?.visibility = GONE
            showLoginAlert(activity as MainActivity?)
        } else {
            rvOrderItems.adapter = CartAdapter(cartItemList, this, this)
            viewModel?.viewCart(
                CommonUtils.getPrefValue(context, PrefConstants.TOKEN), CommonUtils.getPrefValue(
                    context,
                    PrefConstants.LATITUDE
                ), CommonUtils.getPrefValue(
                    context,
                    PrefConstants.LONGITUDE
                )
            )

        }


    }

    fun addMore() {
//        navigate to restaurant detail
        val bundle = bundleOf(AppConstants.RESTAURANT_ID to restId)

        view?.findNavController()
            ?.navigate(
                R.id.action_navigationCart_to_restaurantDetailParent, bundle
            )

    }

    fun showPaymentOptionsDialog() {
        var optionsDialog = PaymentOptionsDialog(this)
        optionsDialog.show(this.childFragmentManager, "")
    }

    private fun showLoginAlert(activity: MainActivity?) {
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
                activity.nav_view.setCheckedItem(R.id.navigationRestaurants)
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

    fun showOffers() {
        view?.findNavController()
            ?.navigate(
                R.id.action_navigationCart_to_offerFragment
            )
    }

    private fun applyPromo() {
        if (viewModel?.offerModel?.value != null) {
            discount = String.format(
                "%.2f",
                (viewModel?.cartData?.value?.cartSubTotal?.toDouble()!! * viewModel?.offerModel?.value!!.percentage!!.toDouble()!! / 100)
            )
            viewModel?.cartData?.value?.totalPrice = String.format(
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
            tv_total.text = viewModel?.cartData?.value?.totalPrice
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
        } else if (resultCode == RESULT_CANCELED) {
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
            if (addMoreList[position2].itemCategories?.size!! > 0) {

            } else {

            }
        } else {
            viewModel?.updateCartQty(
                viewModel?.token?.value!!,
                cartItemList.get(position).id!!,
                cartItemList.get(position).cartId!!,
                "1"
            )
        }
    }

    override fun minus(position: Int, position2: Int) {
        if (position == -1) {

        } else {
            viewModel?.updateCartQty(
                viewModel?.token?.value!!,
                cartItemList.get(position).id!!,
                cartItemList.get(position).cartId!!,
                "-1"
            )
        }
    }

    private fun setPromo() {
        viewModel?.promoId?.value = viewModel?.offerModel?.value!!.percentage!!.toString()

        tv_dis.text = viewModel?.offerModel?.value!!.code
        tv_promo.text = getString(R.string.promo_applied)
        tv_promo.setTextColor(ContextCompat.getColor(context!!, R.color.green))
        img_arrow.visibility = GONE
        tv_remove.visibility = VISIBLE

        applyPromo()
    }

    fun removePromo() {
        viewModel?.promoId?.value = "0"
        tv_dis.text = getString(R.string.apply_promocode)
        tv_promo.text = getString(R.string.no_promo_selected)
        tv_promo.setTextColor(ContextCompat.getColor(context!!, R.color.textGrey))
        img_arrow.visibility = VISIBLE
        tv_remove.visibility = GONE

        if (viewModel?.offerModel?.value != null) {
            discount = "0"

            viewModel?.cartData?.value?.totalPrice = String.format(
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
            tv_total.text = viewModel?.cartData?.value?.totalPrice
        } else {
            CommonUtils.showMessage(view, getString(R.string.no_promo_selected))
        }


//        applyPromo()

    }

    override fun onItemClick(item: Any) {
        if (item is Int) {
            if (item != 0) {
                viewModel?.paymentMode?.value = item.toString()
            }
        }
    }

    fun createLink(){
        val buo = BranchUniversalObject()
            .setCanonicalIdentifier("content/12345")
            .setTitle("Grigoa Group Order")
            .setContentDescription("You are invited from Amit to order from this restaurant")
            .setContentImageUrl("http://3.13.78.53/GriGora/public/images/grigora.png")
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("orderId", "123"))

        val lp = LinkProperties()
            .setChannel("facebook")
            .setFeature("sharing")
            .setCampaign("content 123 launch")
            .setStage("new user")
            .addControlParameter("custom_random", Calendar.getInstance().timeInMillis.toString())

        buo.generateShortUrl(context!!, lp
        ) { url, error ->
            if (error == null) {
                tv_link.text = url
            } else {
            }
        }

        val ss = ShareSheetStyle(activity as MainActivity, "Check this out!", "This stuff is awesome: ")
            .setCopyUrlStyle(ContextCompat.getDrawable(context!!,android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
            .setMoreOptionStyle(ContextCompat.getDrawable(context!!,android.R.drawable.ic_menu_search), "Show more")
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.WHATS_APP)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK_MESSENGER)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.FLICKR)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.PINTEREST)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER)
            .setAsFullWidthStyle(true)
            .setSharingTitle("Share With")

        buo.showShareSheet(activity as MainActivity, lp, ss, object : Branch.BranchLinkShareListener {
            override fun onShareLinkDialogLaunched() {}
            override fun onShareLinkDialogDismissed() {}
            override fun onLinkShareResponse(sharedLink: String?, sharedChannel: String?, error: BranchError?) {}
            override fun onChannelSelected(channelName: String) {}
        })
    }

}
