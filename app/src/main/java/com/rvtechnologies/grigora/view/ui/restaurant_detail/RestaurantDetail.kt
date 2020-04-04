package com.rvtechnologies.grigora.view.ui.restaurant_detail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.RestaurantDetailFragmentBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.FeaturedAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.ParentsAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.MealsAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.PromotionsAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.FeaturedModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.SheetTypeModel
import com.rvtechnologies.grigora.view_model.RestaurantDetailViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_restaurant_detail_parent.*
import kotlinx.android.synthetic.main.restaurant_detail_fragment.*

class RestaurantDetail(
    var restaurantId: String,
    val iRecyclerItemClick: IRecyclerItemClick
) : Fragment(), IRecyclerItemClick {
    private var EXPANDED = "expanded"
    private var COLLAPESD = "collapsed"
    lateinit var restaurantDetailModel: RestaurantDetailNewModel
    private var cartId = ""

    private lateinit var viewModel: RestaurantDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val restaurantDetailFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.restaurant_detail_fragment,
            container,
            false
        ) as RestaurantDetailFragmentBinding
        restaurantDetailFragmentBinding.restaurantDetailFragment = this
        return restaurantDetailFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RestaurantDetailViewModel::class.java)

        viewModel.restaurantDetail.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {

                if (response.status!!) {
                    restaurantDetailModel = response.data as RestaurantDetailNewModel
//                    disable pickup/delivery if restaurant is unavailable or closed
                    handleClosed()

                    setPreviousOrder()
                    setFeatured()
                    setPopular()
                    setPromotions()
                    setMenu()


                    if (!restaurantDetailModel.order_type.isNullOrEmpty() && restaurantDetailModel.order_type == "1") {
                        tv_delivery.callOnClick()
                    } else {
                        tv_pickup.callOnClick()
                    }

                    if (!restaurantDetailModel.cart_id.isNullOrEmpty()) {
                        cartId = restaurantDetailModel.cart_id
                    }

                    if (restaurantDetailModel.full_time == "1") {
                        tv_tt.text = getString(R.string.open_24_hours)
                        tv_time.visibility = View.GONE
                    } else
                        tv_time.text = CommonUtils.getFormattedUtc(
                            restaurantDetailModel.opening_time,
                            "HH:mm:ss",
                            "hh:mm aa"
                        ) + " to " + CommonUtils.getFormattedUtc(
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



                    if (AppConstants.CURRENT_SELECTED == 0)
                        updateCartButton()


                } else {
                    CommonUtils.showMessage(parentpager, response.toString())
                }
            } else {
                CommonUtils.showMessage(parentpager, response.toString())
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
        viewModel.getRestaurantsDetails(
            restaurantId,
            ""
        )
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

    private fun handleClosed() {
        //        not always opened
        if (restaurantDetailModel.full_time == "0") {
            if (!CommonUtils.isRestaurantOpen(
                    restaurantDetailModel.opening_time,
                    restaurantDetailModel.closing_time
                )
            ) {
//                restaurant is closed
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
            li_enabled.visibility = View.GONE
            li_disabled.visibility = View.VISIBLE

            li_time.visibility = View.GONE
            li_status.visibility = View.VISIBLE

            tv_status.text = getString(R.string.busy)
            tv_pickup_desc.visibility = View.GONE

        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
    }

    fun pickupClicked() {
        tv_pickup.setBackgroundResource(R.drawable.pickup_sel)
        tv_delivery.setBackgroundResource(R.drawable.delivery_de_sel)
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

    fun deliveryClicked() {
        tv_pickup.setBackgroundResource(R.drawable.pickup_de_sel)
        tv_delivery.setBackgroundResource(R.drawable.delivery_sel)
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

    private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color=$color>$text</font>"
    }

    fun chooseType() {

        var list = ArrayList<RestaurantDetailNewModel.AllData>()
        list.addAll(restaurantDetailModel.all_data)

        var sheet = ChooseTypeSheet(list, this)
        sheet.show(childFragmentManager, "")
    }

    fun scheduleClicked() {
        if (CommonUtils.isLogin()) {
            if ((activity as MainActivity).fab_cart.visibility == View.VISIBLE)
                iRecyclerItemClick.onItemClick(3)
            else
                CommonUtils.showMessage(parentpager, getString(R.string.please_add_items))
        } else
            (activity as MainActivity).showLoginAlert()
    }

    fun reviewClick() {
        if (restaurantDetailModel.total_review > 0)
            iRecyclerItemClick.onItemClick(2)
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
                var sheet = MealDetailSheet(item)
                sheet.show(childFragmentManager, "")
            }
//            else -> view?.findNavController()?.navigate(R.id.action_restaurantDetail_to_mealsList)
        }
    }

    private fun updateCartButton() {

        if (::restaurantDetailModel.isInitialized && restaurantDetailModel.normal_cart != null) {
            AppConstants.CART_RESTAURANT = restaurantDetailModel.normal_cart!!.restaurant_name
            AppConstants.CART_COUNT = restaurantDetailModel.normal_cart!!.quantity
            (activity as MainActivity).updateCartButton()
        }
    }
}