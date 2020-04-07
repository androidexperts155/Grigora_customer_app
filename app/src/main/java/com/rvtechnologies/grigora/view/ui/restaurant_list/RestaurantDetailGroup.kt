package com.rvtechnologies.grigora.view.ui.restaurant_list

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.RestaurantDetailGroupFragmentBinding
import com.rvtechnologies.grigora.model.AddCartModel
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.groupCart.GroupOrderAlreadyPlaced
import com.rvtechnologies.grigora.view.ui.restaurant_detail.ChooseTypeSheet
import com.rvtechnologies.grigora.view.ui.restaurant_detail.MealDetailSheet
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.FeaturedAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.MealsAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.ParentsAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.PromotionsAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.SheetTypeModel
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.ItemsCartAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantDetailAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantItemAdapter
import com.rvtechnologies.grigora.view_model.RestaurantDetailGroupViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_login.view.*
import kotlinx.android.synthetic.main.existing_cart_dialog.view.*
import kotlinx.android.synthetic.main.restaurant_detail_group_fragment.*
import kotlinx.android.synthetic.main.restaurant_detail_group_fragment.tv_restname

class RestaurantDetailGroup : Fragment(), IRecyclerItemClick, MealDetailSheet.Refresh {
    private var EXPANDED = "expanded"
    private var COLLAPESD = "collapsed"

    lateinit var menuItemModel: MenuItemModel
    private lateinit var fragmentRestaurantsDetailsBinding: RestaurantDetailGroupFragmentBinding
    private lateinit var viewModel: RestaurantDetailGroupViewModel
    private var restaurantId = ""
    private var cartId = ""
    lateinit var restaurantDetailModel: RestaurantDetailNewModel

    companion object {
        fun newInstance() = RestaurantDetailGroup()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restaurantId = arguments?.get(AppConstants.RESTAURANT_ID).toString()
        cartId = arguments?.get(AppConstants.CART_ID).toString()

        viewModel = ViewModelProviders.of(this).get(RestaurantDetailGroupViewModel::class.java)


        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

        viewModel.restaurantDetail.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    restaurantDetailModel = response.data as RestaurantDetailNewModel
                    handleClosed()
                    setPreviousOrder()
                    setFeatured()
                    setPopular()
                    setPromotions()
                    setMenu()
                    handleGroup(restaurantDetailModel)



                    tv_restname.text = restaurantDetailModel.restaurant_name
                    if (!restaurantDetailModel.order_type.isNullOrEmpty() && restaurantDetailModel.order_type == "1")
                        tv_delivery.callOnClick()
                    else
                        tv_pickup.callOnClick()



                    if (restaurantDetailModel.full_time.equals("1")) {
                        tv_tt.text = getString(R.string.open_24_hours)
                        tv_time.visibility = View.GONE
                    } else
                        tv_time.text = CommonUtils.getFormattedTimeOrDate(
                            restaurantDetailModel.opening_time,
                            "HH:mm:ss",
                            "hh:mm aa"
                        ) + " to " + CommonUtils.getFormattedTimeOrDate(
                            restaurantDetailModel.closing_time,
                            "HH:mm:ss",
                            "hh:mm aa"
                        )

                    CommonUtils.loadImage(img_rest, restaurantDetailModel.restaurant_image)
                    CommonUtils.loadImage(img_wall, restaurantDetailModel.restaurant_image)
                    tv_address.text = restaurantDetailModel.address
                    tv_rating.text = restaurantDetailModel.total_rating
                    tv_reviews.text = restaurantDetailModel.total_review.toString()
                    tv_restaurantname.text = restaurantDetailModel.restaurant_name
                    tv_cuisines.text = restaurantDetailModel.cuisines


//                    handle shimmer
                    li_shimmer.visibility = View.GONE
                    shimmer_image.visibility = View.GONE
                    li_main.visibility = View.VISIBLE
                    img_wall.visibility = View.VISIBLE


                } else {
                    CommonUtils.showMessage(parentView, response.toString())
                }
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

        /*  viewModel.getRestaurantsDetailsCart(
              CommonUtils.getPrefValue(
                  context,
                  PrefConstants.TOKEN
              ),
              restaurantId,
              "", cartId
          )*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRestaurantsDetailsBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.restaurant_detail_group_fragment,
                container,
                false
            )
        fragmentRestaurantsDetailsBinding.restaurantDetailsView = this
        fragmentRestaurantsDetailsBinding.restauranDetailsViewModel = viewModel
        return fragmentRestaurantsDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        li_disabled.setOnClickListener {
            CommonUtils.showMessage(parentView, getString(R.string.unavailable_func))
        }
    }

    private fun handleGroup(data: RestaurantDetailNewModel) {
        viewModel.cartId.value = data.cart?.id.toString()
        if (data.cart != null && data.cart?.quantity!! > 0) {
            fab_cart_group.visibility = View.VISIBLE
        } else if (data.cart_id.isNullOrEmpty()) {
            var groupOrderAlreadyPlaced = GroupOrderAlreadyPlaced(this)
            groupOrderAlreadyPlaced.show(childFragmentManager, "")
        }

        if (arguments?.containsKey(AppConstants.IS_FOR_GROUP_ORDER)!!) {
            if (data?.cart != null) {
                var inviteFragment = InviteFragment(data.cart?.share_link!!)
                inviteFragment.show(childFragmentManager, "")
            }
        }

        if (data?.cart != null) {

            rel_invite.visibility = View.VISIBLE

            if (data?.cart!!.user_id.toString() == CommonUtils.getPrefValue(
                    context!!,
                    PrefConstants.ID
                )
            ) {
                tv_group_order_title.text =
                    "${getString(R.string.group_order_by)} ${data?.cart!!.name}"
                tv_order_limit.text =
                    "₦ ${data?.cart!!.max_per_person} ${getString(R.string.per_person_limit)}"
            } else {
                tv_group_order_title.text =
                    "${data?.cart!!.name}'s ${getString(R.string.group_order)}"
                tv_order_limit.text =
                    "₦ ${data?.cart!!.max_per_person} ${getString(R.string.per_person_limit)}"
            }

        }
    }

    private fun handleClosed() {
        //        not always opened
        if (restaurantDetailModel.full_time == "0") {
            if (!CommonUtils.isRestaurantOpen(
                    restaurantDetailModel.opening_time,
                    restaurantDetailModel.closing_time
                )
            ) {
//                restaurant is closed
                tv_delivery_fee.visibility = View.GONE
                li_enabled.visibility = View.GONE
                li_disabled.visibility = View.VISIBLE

                li_time.visibility = View.GONE
                li_status.visibility = View.VISIBLE

                tv_status.text = getString(R.string.closed)
                tv_pickup_desc.visibility = View.GONE

            }
        }
        if ((CommonUtils.isRestaurantOpen(
                restaurantDetailModel.opening_time,
                restaurantDetailModel.closing_time
            ) || restaurantDetailModel.full_time == "1") && restaurantDetailModel.busy_status == "1"
        ) {
//restaurant is busy
            tv_delivery_fee.visibility = View.GONE
            li_enabled.visibility = View.GONE
            li_disabled.visibility = View.VISIBLE

            li_time.visibility = View.GONE
            li_status.visibility = View.VISIBLE

            tv_status.text = getString(R.string.busy)
            tv_pickup_desc.visibility = View.GONE

        }
    }

    private fun setPromotions() {
        if (restaurantDetailModel.promo.isNullOrEmpty()) {
            li_promotion.visibility = View.GONE
        } else {
            var list = ArrayList<RestaurantDetailNewModel.Promo>()
            list.addAll(restaurantDetailModel.promo)
            rc_promo.adapter = PromotionsAdapter(list, this)
            li_promotion.visibility = View.VISIBLE
        }
    }

    private fun setFeatured() {
        if (restaurantDetailModel.featured_items.isNullOrEmpty()) {
            li_featured.visibility = View.GONE
        } else {
            var list = ArrayList<RestaurantDetailNewModel.MealItem>()
            list.addAll(restaurantDetailModel.featured_items)
            if (list.size <= 3) {
                var layoutManager = LinearLayoutManager(context!!)
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
//                set horizontal
                rec_featured.layoutManager = layoutManager
            }
            rec_featured.adapter = FeaturedAdapter(list, this)
            li_featured.visibility = View.VISIBLE
        }
    }

    private fun setPreviousOrder() {
        if (restaurantDetailModel.previous_ordered_items.isNullOrEmpty()) {
            li_previously_orderd.visibility = View.GONE
        } else {
            var list = ArrayList<RestaurantDetailNewModel.MealItem>()
            list.addAll(restaurantDetailModel.previous_ordered_items)
            rec_previously.adapter = MealsAdapter(list, this)

            collapse_previously.setOnClickListener {
                if (it.tag == COLLAPESD) {
                    it.tag = EXPANDED
                    it.rotation = 0F
                    rec_previously.visibility = View.VISIBLE
                } else {
                    rec_previously.visibility = View.GONE
                    it.tag = COLLAPESD
                    it.rotation = -90F
                }
            }
            li_previously_orderd.visibility = View.VISIBLE
        }
    }

    private fun setPopular() {
        if (restaurantDetailModel.popular_items.isNullOrEmpty()) {
            li_popular.visibility = View.GONE
        } else {
            var list = ArrayList<RestaurantDetailNewModel.MealItem>()
            list.addAll(restaurantDetailModel.popular_items)
            rec_popular.adapter = MealsAdapter(list, this)

            collapse_popular.setOnClickListener {
                if (it.tag == COLLAPESD) {
                    it.tag = EXPANDED
                    it.rotation = 0F
                    rec_popular.visibility = View.VISIBLE
                } else {
                    rec_popular.visibility = View.GONE
                    it.tag = COLLAPESD
                    it.rotation = -90F
                }
            }

            li_popular.visibility = View.VISIBLE
        }
    }

    private fun setMenu() {
        bt_type.text = restaurantDetailModel.all_data[0].category_name
        var list = ArrayList<RestaurantDetailNewModel.AllData.Data>()
        list.addAll(restaurantDetailModel.all_data[0].data)
        rec_parents.adapter = ParentsAdapter(list, this)
    }

    private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color=$color>$text</font>"
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle(getString(R.string.group_order))
            (activity as MainActivity).lockDrawer(true)

            (activity as MainActivity).img_back.setOnClickListener {
                view?.findNavController()
                    ?.navigate(
                        R.id.action_restaurantDetailGroup_to_dashboardFragment
                    )
            }
        }
        viewModel.getRestaurantsDetailsCart(
            CommonUtils.getPrefValue(
                context,
                PrefConstants.TOKEN
            ),
            restaurantId,
            "", cartId
        )
        (activity as MainActivity).fab_cart.visibility = View.GONE
    }

    fun reviewClick() {
        val bundle = bundleOf(AppConstants.RESTAURANT_ID to viewModel.id.value)
        view?.findNavController()
            ?.navigate(R.id.action_restaurantDetailGroup_to_reviewsFragment, bundle)
    }

    fun deliveryClicked() {
        tv_pickup.setBackgroundResource(R.drawable.pickup_de_sel)
        tv_delivery.setBackgroundResource(R.drawable.delivery_sel)
        tv_delivery_fee.text =
            getString(R.string.delivey_fee) + " ${restaurantDetailModel.delivery_fee}"
        tv_delivery_fee.visibility = View.VISIBLE
        if (CommonUtils.isDarkMode()) {
            tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        } else {
            tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        }

//        functionality

        var distance = CommonUtils.calculateDistance(
            restaurantDetailModel.latitude.toDouble(),
            restaurantDetailModel.longitude.toDouble(),
            CommonUtils.getPrefValue(context, PrefConstants.LATITUDE).toDouble(),
            CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE).toDouble()
        )

        tv_pickup_desc.visibility = View.VISIBLE
        var color: String = if (CommonUtils.isDarkMode()) "#ffffff" else "#262626"
        val distanceM = getColoredSpanned(
            "${CommonUtils.getRoundedOff(
                distance.toDouble()
            )} " + getString(
                R.string.km_away
            ), color
        )

        tv_pickup_desc.text = Html.fromHtml(distanceM)

        var t = restaurantDetailModel.estimated_preparing_time.toInt() + (distance * 2)

        val hours: Int =
            t.toInt() / 60 //since both are ints, you get an int

        val minutes: Int = t.toInt() % 60

        if (hours > 0) {
            tv_deliver.text =
                getString(R.string.delivers_in) + " $hours hours and $minutes minutes"
        } else {
            tv_deliver.text =
                getString(R.string.delivers_in) + " $minutes minutes"
        }
        if (restaurantDetailModel.order_type != "1")
            viewModel.updateType(
                restaurantId,
                "1",
                CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
            )
    }

    fun pickupClicked() {
        tv_pickup.setBackgroundResource(R.drawable.pickup_sel)
        tv_delivery.setBackgroundResource(R.drawable.delivery_de_sel)
        tv_delivery_fee.visibility = View.GONE
        if (CommonUtils.isDarkMode()) {
            tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        } else {
            tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
        }

        var distance = CommonUtils.calculateDistance(
            restaurantDetailModel.latitude.toDouble(),
            restaurantDetailModel.longitude.toDouble(),
            CommonUtils.getPrefValue(context, PrefConstants.LATITUDE).toDouble(),
            CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE).toDouble()
        )

        var color: String = if (CommonUtils.isDarkMode()) "#ffffff" else "#262626"
        val address = getColoredSpanned(restaurantDetailModel.address + ". ", "#D01110")
        val info = getColoredSpanned(getString(R.string.pick_order_info), color)
        val distanceM = getColoredSpanned(
            "${CommonUtils.getRoundedOff(
                distance.toDouble()
            )} " + getString(
                R.string.km_away
            ), color
        )

        tv_pickup_desc.text = Html.fromHtml(info + " " + address + " " + distanceM)


        val hours: Int =
            restaurantDetailModel.estimated_preparing_time.toInt() / 60 //since both are ints, you get an int

        val minutes: Int = restaurantDetailModel.estimated_preparing_time.toInt() % 60


        if (hours > 0) {
            tv_deliver.text =
                getString(R.string.preparein) + " $hours hours and $minutes minutes"
        } else {
            tv_deliver.text =
                getString(R.string.preparein) + " $minutes minutes"
        }

        if (restaurantDetailModel.order_type != "2")
            viewModel.updateType(
                restaurantId,
                "2",
                CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
            )

    }

    fun chooseType() {

        var list = ArrayList<RestaurantDetailNewModel.AllData>()
        list.addAll(restaurantDetailModel.all_data)

        var sheet = ChooseTypeSheet(list, this)
        sheet.show(childFragmentManager, "")
    }

    override fun onItemClick(item: Any) {
        when (item) {
            is RestaurantDetailNewModel.AllData -> {
                bt_type.text = item.category_name
                var list = ArrayList<RestaurantDetailNewModel.AllData.Data>()
                list.addAll(restaurantDetailModel.all_data[0].data)
                rec_parents.adapter = ParentsAdapter(list, this)
            }
            is RestaurantDetailNewModel.MealItem -> {
                var sheet = MealDetailSheet(item, cartId,this)
                sheet.show(childFragmentManager, "")
            }
            is RestaurantDetailNewModel.AllData.Data -> {
                val bundle =
                    bundleOf(AppConstants.CUISINE_ID to item.id, AppConstants.CART_ID to cartId)
                view?.findNavController()
                    ?.navigate(
                        R.id.action_restaurantDetailGroup_to_mealsList,
                        bundle
                    )
            }
        }



        if (item is MenuItemModel) {
            menuItemModel = item
            menuItemModel.isForGroupCart = true
            menuItemModel.cartId = viewModel.cartId.value.toString()

//            if (item.itemCategories!!.isNotEmpty()) {
            item.isForGroupCart = true
            item.cartId = viewModel.cartId.value.toString()
            val bundle = bundleOf(AppConstants.MENU_ITEM_MODEL to item)


            view?.findNavController()
                ?.navigate(
                    R.id.action_restaurantDetailGroup_to_menuItemDetailsFragment,
                    bundle
                )
//            }
        } else if (item is Int) {
            view?.findNavController()
                ?.navigate(
                    R.id.action_restaurantDetailGroup_to_dashboardFragment
                )
        }
    }

    fun invite() {

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            restaurantDetailModel.cart?.share_link
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    fun showcart() {
        val bundle = bundleOf(AppConstants.CART_ID to viewModel.cartId.value.toString())
        view?.findNavController()
            ?.navigate(
                R.id.action_restaurantDetailGroup_to_GroupCart, bundle
            )
    }

    fun schedule() {
        if (CommonUtils.isLogin()) {
//            if ((activity as MainActivity).fab_cart.visibility == View.VISIBLE)
//                iRecyclerItemClick.onItemClick(3)
//            else
//                CommonUtils.showMessage(parentView, getString(R.string.please_add_items))


        } else
            (activity as MainActivity).showLoginAlert()
    }

    override fun refresh(refresh: Boolean) {
        if (refresh) {
            viewModel.getRestaurantsDetailsCart(
                CommonUtils.getPrefValue(
                    context,
                    PrefConstants.TOKEN
                ),
                restaurantId,
                "", cartId
            )
        }
    }
}


