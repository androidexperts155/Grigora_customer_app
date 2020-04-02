package com.rvtechnologies.grigora.view.ui.restaurant_list

import android.app.AlertDialog
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
import com.facebook.common.Common
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.RestaurantDetailsFragmentBinding
import com.rvtechnologies.grigora.model.AddCartModel
import com.rvtechnologies.grigora.model.RestaurantDetailModel
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.ItemsCartAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantDetailAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantItemAdapter
import com.rvtechnologies.grigora.view_model.CartSharedViewModel
import com.rvtechnologies.grigora.view_model.RestaurantDetailsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.existing_cart_dialog.view.*
import kotlinx.android.synthetic.main.restaurant_details_fragment.*

class RestaurantDetailsFragment(
    var restaurantId: String,
    val iRecyclerItemClick: IRecyclerItemClick
) : Fragment(), OnItemClickListener,
    QuantityClicks,
    QuantityClicksDialog,
    IRecyclerItemClick {
    private val cartItemList = ArrayList<CartDetail>()
    lateinit var menuItemModel: MenuItemModel
    private lateinit var fragmentRestaurantsDetailsBinding: RestaurantDetailsFragmentBinding
    private lateinit var viewModel: RestaurantDetailsViewModel
    private var mealsAndCuisinesList = ArrayList<RestaurantDetailModel.AllData>()
    private var filteredMealsAndCuisinesList = ArrayList<RestaurantDetailModel.AllData>()
    private val popularList = ArrayList<MenuItemModel>()
    private val previousList = ArrayList<MenuItemModel>()
    private var cartId = ""
    var count = 0
    lateinit var restaurantDetailModel: RestaurantDetailModel
    var filter = ""
    lateinit var cartSharedViewModel: CartSharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartSharedViewModel =
            activity!!.let { ViewModelProviders.of(it).get(CartSharedViewModel::class.java) }


        viewModel = ViewModelProviders.of(this).get(RestaurantDetailsViewModel::class.java)

        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

        viewModel.restaurantDetail.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                mealsAndCuisinesList.clear()
                filteredMealsAndCuisinesList.clear()

                if (response.status!!) {
                    restaurantDetailModel = response.data as RestaurantDetailModel

//                    disable pickup/delivery if restaurant is unavailable or closed
                    handleClosed()

                    if (!restaurantDetailModel.orderType.isNullOrEmpty() && restaurantDetailModel.orderType == "1") {
                        tv_delivery.callOnClick()
                    } else {
                        tv_pickup.callOnClick()
                    }

                    if (!restaurantDetailModel.cart_id.isNullOrEmpty()) {
                        cartId = restaurantDetailModel.cart_id
                    }

                    if (restaurantDetailModel.popluarItems.size == 0) {
                        li_popular.visibility = View.GONE
                    } else {
                        popularList.clear()
                        popularList.addAll(restaurantDetailModel.popluarItems)
                        rc_popular.adapter!!.notifyDataSetChanged()
                        tv_popular_count.text = restaurantDetailModel.popluarItems.size.toString()
                    }

                    if (restaurantDetailModel.previousOrderedItems.size == 0) {
                        li_previous.visibility = View.GONE
                    } else {
                        previousList.clear()

                        previousList.addAll(restaurantDetailModel.previousOrderedItems)
                        tv_pre_count.text =
                            restaurantDetailModel.previousOrderedItems.size.toString()
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

                    tv_rating.text = restaurantDetailModel.total_rating
                    tv_reviews.text = restaurantDetailModel.total_review.toString()
                    tv_restaurantname.text = restaurantDetailModel.restaurant_name
                    tv_cuisines.text = restaurantDetailModel.cuisines

                    mealsAndCuisinesList.addAll((restaurantDetailModel.allData))
                    filteredMealsAndCuisinesList.addAll(mealsAndCuisinesList)
                    rc_items?.adapter?.notifyDataSetChanged()

                    if (!filter.isNullOrEmpty())
                        filter()

                    if (AppConstants.CURRENT_SELECTED == 0)
                        updateCartButton()
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

        viewModel.cartItemList.observe(this, Observer {

            if (it is CommonResponseModel<*>) {
                cartItemList.clear()
                cartItemList.addAll((it as CommonResponseModel<*>).data as ArrayList<CartDetail>)

                var alertDialog: AlertDialog? = null

                val dialogBuilder = activity?.let { AlertDialog.Builder(it) }
                if (activity is MainActivity && !(activity as MainActivity).isDestroyed && alertDialog == null) {
                    val inflater = (activity as MainActivity).layoutInflater
                    val dialogView = inflater.inflate(R.layout.existing_cart_dialog, null)
                    dialogBuilder?.setView(dialogView)
                    dialogBuilder?.setCancelable(false)

                    dialogView.tv_title.text =
                        cartItemList[0].itemName


                    dialogView.rvOrderItems.adapter =
                        ItemsCartAdapter(
                            cartItemList,
                            this@RestaurantDetailsFragment, this
                        )
                    dialogView.bt_add_new.setOnClickListener {
                        alertDialog?.dismiss()
                        val bundle = bundleOf(AppConstants.MENU_ITEM_MODEL to menuItemModel)
                        iRecyclerItemClick.onItemClick(bundle)
                    }

                    dialogView.img_close.setOnClickListener {
                        alertDialog?.dismiss()

                        viewModel.getRestaurantsDetails(
                            CommonUtils.getPrefValue(
                                context,
                                PrefConstants.TOKEN
                            ),
                            restaurantId,
                            ""
                        )
                    }
                    alertDialog = dialogBuilder?.create()
                    alertDialog?.show()
                }
            } else {
                CommonUtils.showMessage(parentView, it.toString())
            }
        })

        viewModel.addCartRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    var data = response.data as AddCartModel
                    cartId = data.cartId.toString()
                    if (data.quantity > 0)
                        AppConstants.CART_COUNT = data.quantity


                    viewModel.getRestaurantsDetails(
                        CommonUtils.getPrefValue(
                            context,
                            PrefConstants.TOKEN
                        ),
                        restaurantId,
                        ""
                    )
                    (activity as MainActivity).updateCartButton()
                } else {
                    CommonUtils.showMessage(parentView, response.message!!)
                    viewModel.getRestaurantsDetails(
                        CommonUtils.getPrefValue(
                            context,
                            PrefConstants.TOKEN
                        ),
                        restaurantId,
                        ""
                    )
                }

            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRestaurantsDetailsBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.restaurant_details_fragment,
                container,
                false
            )
        fragmentRestaurantsDetailsBinding.restaurantDetailsView = this
        fragmentRestaurantsDetailsBinding.restauranDetailsViewModel = viewModel
        return fragmentRestaurantsDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //      -1 states that it is popular
        rc_popular.adapter = RestaurantItemAdapter(popularList, this, this, -1)
        rc_popular.isNestedScrollingEnabled = false

//      -2 states that it is previous ordered
//        rc_previous.adapter = RestaurantItemAdapter(previousList, this, this, -2)
//        rc_previous.isNestedScrollingEnabled = false

        rc_items.adapter = RestaurantDetailAdapter(filteredMealsAndCuisinesList, this, this)
        rc_items.isNestedScrollingEnabled = false

        initFilter()
        initSearchView()
        manageSwitch()

        li_disabled.setOnClickListener {
            CommonUtils.showMessage(parentView, getString(R.string.unavailable_func))
        }
//        if(arguments?.get(AppConstants.FROM_PICKUP) as Boolean){
//            tv_pickup.callOnClick()
//        }
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
            ) || restaurantDetailModel.full_time == "1") && restaurantDetailModel.busyStatus == "1"
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

    private fun manageSwitch() {
        tv_delivery.setOnClickListener {
            var distance = CommonUtils.calculateDistance(
                restaurantDetailModel.latitude.toDouble(),
                restaurantDetailModel.longitude.toDouble(),
                CommonUtils.getPrefValue(context, PrefConstants.LATITUDE).toDouble(),
                CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE).toDouble()
            )

            tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_delivery.setBackgroundResource(R.drawable.delivery_sel)
            tv_pickup.setBackgroundResource(R.drawable.pickup_de_sel)
            tv_pickup_desc.visibility = View.VISIBLE

            tv_pickup_desc.visibility = View.VISIBLE


            var color: String = if (CommonUtils.isDarkMode()) "#ffffff" else "#262626"
//            val address = getColoredSpanned(restaurantDetailModel.address + ". ", "#D01110")
//            val info = getColoredSpanned(getString(R.string.pick_order_info), color)
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





            if (CommonUtils.isDarkMode()) {
                tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            } else
                tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

            if (restaurantDetailModel.orderType != "1")
                viewModel.updateType(
                    restaurantId,
                    "1",
                    CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
                )
        }

        tv_pickup.setOnClickListener {
//            if (CommonUtils.isLogin()) {
            tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_pickup.setBackgroundResource(R.drawable.pickup_sel)

            tv_delivery.setBackgroundResource(R.drawable.delivery_de_sel)
            tv_pickup_desc.visibility = View.VISIBLE

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


            if (CommonUtils.isDarkMode()) {
                tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            } else
                tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))

            if (restaurantDetailModel.orderType != "2")
                viewModel.updateType(
                    restaurantId,
                    "2",
                    CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
                )
//            } else
//                (activity as MainActivity).showLoginAlert()
        }

    }

    private fun getColoredSpanned(text: String, color: String): String? {
        return "<font color=$color>$text</font>"
    }

    private fun initFilter() {
        rg_veg_nonveg.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rd_veg -> {
                    filter = "1"
                }
                R.id.rd_nonveg -> {
                    filter = "0"
                }
                R.id.rd_containsegg -> {
                    filter = "2"
                }
            }

            filter()
        }
    }

    fun filter() {
        filteredMealsAndCuisinesList.clear()

        for (cuisineIndex in 0 until mealsAndCuisinesList.size) {
            var model = RestaurantDetailModel.AllData()
            model.name = mealsAndCuisinesList[cuisineIndex].name
            filteredMealsAndCuisinesList.add(model)

            for (mealIndex in mealsAndCuisinesList[cuisineIndex].items) {
                if (mealIndex.pureVeg == filter) {
                    filteredMealsAndCuisinesList[cuisineIndex].items.add(mealIndex)
                }
            }
        }

        var tempList = ArrayList<RestaurantDetailModel.AllData>()
        if (filteredMealsAndCuisinesList.size != 0) {
            for (cusine in filteredMealsAndCuisinesList) {
                if (cusine.items.size == 0) {
                    tempList.add(cusine)
                }
            }
            filteredMealsAndCuisinesList.removeAll(tempList)
            rc_items.adapter?.notifyDataSetChanged()
        }
    }

    fun reviewClick() {
        if (restaurantDetailModel.total_review > 0)
            iRecyclerItemClick.onItemClick(2)
    }

    fun scheduleOrder() {
        if (CommonUtils.isLogin()) {
            if ((activity as MainActivity).fab_cart.visibility == View.VISIBLE)
                iRecyclerItemClick.onItemClick(3)
            else
                CommonUtils.showMessage(parentView, getString(R.string.please_add_items))
        } else
            (activity as MainActivity).showLoginAlert()
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).lockDrawer(true)
        }
        viewModel.getRestaurantsDetails(
            CommonUtils.getPrefValue(
                context,
                PrefConstants.TOKEN
            ),
            restaurantId,
            ""
        )
        if (AppConstants.CURRENT_SELECTED == 0)
            updateCartButton()

    }

    fun back() {
        view?.findNavController()?.popBackStack()
    }

    override fun add(position: Int, position2: Int) {
//     position=   -2 previous ordered, -1 popular
//        if (CommonUtils.isLogin())
        if (position == -1) {
            if (popularList[position2].itemCategories?.size!! > 0) {
//                have add ons
                if (popularList[position2].item_count_in_cart!! > 0) {
//                already have added before, call api and get what is added
                    showItems(popularList[position2])
                } else {
//                show item details screen
                    val bundle =
                        bundleOf(AppConstants.MENU_ITEM_MODEL to popularList[position2])
                    iRecyclerItemClick.onItemClick(bundle)
                }
            } else {
//                don't have add ons, simply add
//                        popularList[position2].item_count_in_cart =
//                            (popularList[position2].item_count_in_cart!! + 1)

                viewModel.addItemToCart(
                    popularList[position2].restaurantId.toString()!!,
                    popularList[position2].id.toString(),
                    popularList[position2].price.toString(),
                    "1"
                )

                rc_popular.adapter?.notifyDataSetChanged()
            }

        } else if (position == -2) {
            if (previousList[position2].itemCategories?.size!! > 0) {
//                have add ons
                if (previousList[position2].item_count_in_cart!! > 0) {
//                already have added before, call api and get what is added
                    showItems(previousList[position2])
                } else {
//                show item details screen
                    val bundle =
                        bundleOf(AppConstants.MENU_ITEM_MODEL to previousList[position2])
                    iRecyclerItemClick.onItemClick(bundle)
                }
            } else {
//                don't have add ons, simply add
//                        previousList[position2].item_count_in_cart =
//                            (previousList[position2].item_count_in_cart!! + 1)

                viewModel.addItemToCart(
                    previousList[position2].restaurantId.toString()!!,
                    previousList[position2].id.toString(),
                    previousList[position2].price.toString(),
                    "1"
                )

//                rc_previous.adapter?.notifyDataSetChanged()
            }


        } else {
            if (filteredMealsAndCuisinesList[position].items[position2].itemCategories?.size!! > 0) {
//                have add ons
                if (filteredMealsAndCuisinesList[position].items[position2].item_count_in_cart!! > 0) {
//                already have added before, call api and get what is added
                    showItems(filteredMealsAndCuisinesList[position].items[position2])
                } else {
//                show item details screen
                    val bundle =
                        bundleOf(AppConstants.MENU_ITEM_MODEL to filteredMealsAndCuisinesList[position].items[position2])
                    iRecyclerItemClick.onItemClick(bundle)
                }
            } else {
//                don't have add ons, simply add
//                        filteredMealsAndCuisinesList[position].items[position2].item_count_in_cart =
//                            (filteredMealsAndCuisinesList[position].items[position2].item_count_in_cart!! + 1)

                viewModel.addItemToCart(
                    filteredMealsAndCuisinesList[position].items[position2].restaurantId.toString()!!,
                    filteredMealsAndCuisinesList[position].items[position2].id.toString(),
                    filteredMealsAndCuisinesList[position].items[position2].price.toString(),
                    "1"
                )

                rc_items.adapter?.notifyDataSetChanged()
            }
        }
//        else
//            (activity as MainActivity).showLoginAlert()
    }

    override fun minus(position: Int, position2: Int) {
//     position=   -2 previous ordered, -1 popular
//        if (CommonUtils.isLogin())
        if (position == -1) {
            if (popularList[position2].itemCategories?.size!! > 0) {
//                have add ons
                if (popularList[position2].item_count_in_cart!!.toInt() > 0) {
//                already have added before, call api and get what is added
                    showItems(popularList[position2])
                } else {
//                show item details screen
                    val bundle =
                        bundleOf(AppConstants.MENU_ITEM_MODEL to popularList[position2])
                    iRecyclerItemClick.onItemClick(bundle)
                }
            } else {
//                don't have add ons, simply add
//                    popularList[position2].item_count_in_cart =
//                        (popularList[position2].item_count_in_cart!! - 1)

                rc_popular.adapter?.notifyDataSetChanged()
            }
        } else if (position == -2) {
            if (previousList[position2].itemCategories?.size!! > 0) {
//                have add ons
                if (previousList[position2].item_count_in_cart!!.toInt() > 0) {
//                already have added before, call api and get what is added
                    showItems(previousList[position2])
                } else {
//                show item details screen
                    val bundle =
                        bundleOf(AppConstants.MENU_ITEM_MODEL to previousList[position2])
                    iRecyclerItemClick.onItemClick(bundle)
                }
            } else {
//                don't have add ons, simply add
//                    previousList[position2].item_count_in_cart =
//                        (previousList[position2].item_count_in_cart!! - 1)


                viewModel.addItemToCart(
                    previousList[position2].restaurantId.toString()!!,
                    previousList[position2].id.toString(),
                    previousList[position2].price.toString(),
                    "-1"
                )

//                rc_previous.adapter?.notifyDataSetChanged()
            }
        } else {
            if (filteredMealsAndCuisinesList[position].items[position2].itemCategories?.size!! > 0) {
//                have add ons
                if (filteredMealsAndCuisinesList[position].items[position2].item_count_in_cart!!.toInt() > 0) {
//                already have added before, call api and get what is added
                    showItems(filteredMealsAndCuisinesList[position].items[position2])
                } else {
//                show item details screen
                    val bundle =
                        bundleOf(AppConstants.MENU_ITEM_MODEL to filteredMealsAndCuisinesList[position].items[position2])
                    iRecyclerItemClick.onItemClick(bundle)
                }
            } else {
//                don't have add ons, simply add
//                    filteredMealsAndCuisinesList[position].items[position2].item_count_in_cart =
//                        (filteredMealsAndCuisinesList[position].items[position2].item_count_in_cart!! - 1)

                viewModel.addItemToCart(
                    filteredMealsAndCuisinesList[position].items[position2].restaurantId.toString()!!,
                    filteredMealsAndCuisinesList[position].items[position2].id.toString(),
                    filteredMealsAndCuisinesList[position].items[position2].price.toString(),
                    "-1"
                )

                viewModel.updateCartQty(
                    viewModel.token.value!!,
                    filteredMealsAndCuisinesList[position].items[position2].id.toString(),
                    filteredMealsAndCuisinesList[position]?.items[position2]?.itemCart?.get(0)!!.cart_id.toString(),
                    "-1"
                )
                rc_items.adapter?.notifyDataSetChanged()
            }
        }
//        else
//            (activity as MainActivity).showLoginAlert()
    }

    fun showItems(model: MenuItemModel) {
        menuItemModel = model
        viewModel.getCartItems(viewModel.token.value!!, model.id.toString())
    }

    override fun dialogAdd(position: Int) {
//        hit add api
        viewModel.updateCartQty(
            viewModel.token.value!!,
            cartItemList[position].id!!,
            cartItemList[position].cartId!!,
            "1"
        )
    }

    override fun dialogMinus(position: Int) {
//        hit sub api
        viewModel.updateCartQty(
            viewModel.token.value!!,
            cartItemList[position].id!!,
            cartItemList[position].cartId!!,
            "-1"
        )
    }

    override fun onItemClick(item: Any) {
        if (item is MenuItemModel) {
            menuItemModel = item
//            if (item.itemCategories!!.isNotEmpty()) {
            val bundle = bundleOf(AppConstants.MENU_ITEM_MODEL to item)
            iRecyclerItemClick.onItemClick(bundle)
//            }
        }
    }

    private fun initSearchView() {
        search_view.search_input_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filteredMealsAndCuisinesList.clear()
//            filteredMealsAndCuisinesList =  mealsAndCuisinesList.clone() as ArrayList<RestaurantDetailModel.AllData>

                for (cuisineIndex in 0 until mealsAndCuisinesList.size) {
                    var model = RestaurantDetailModel.AllData()
                    model.name = mealsAndCuisinesList[cuisineIndex].name
                    filteredMealsAndCuisinesList.add(model)

                    for (mealIndex in mealsAndCuisinesList[cuisineIndex].items) {
                        if (mealIndex.name?.contains(p0.toString(), true)!!) {
                            filteredMealsAndCuisinesList[cuisineIndex].items.add(mealIndex)
                        }
                    }
                }
                if (filteredMealsAndCuisinesList.size == 0 && p0.toString() == "") {
                    for (cuisineIndex in 0 until mealsAndCuisinesList.size) {
                        var model = RestaurantDetailModel.AllData()
                        model.name = mealsAndCuisinesList[cuisineIndex].name
                        filteredMealsAndCuisinesList.add(model)

                        for (mealIndex in mealsAndCuisinesList[cuisineIndex].items) {
//                            if (mealIndex.name.contains(p0.toString(), true)) {
                            filteredMealsAndCuisinesList[cuisineIndex].items.add(mealIndex)
//                            }
                        }
                    }
                }

                var tempList = ArrayList<RestaurantDetailModel.AllData>()
                if (filteredMealsAndCuisinesList.size != 0) {
                    for (cusine in filteredMealsAndCuisinesList) {
                        if (cusine.items.size == 0) {
                            tempList.add(cusine)
                        }
                    }
                    filteredMealsAndCuisinesList.removeAll(tempList)
                    rc_items.adapter?.notifyDataSetChanged()
                }
            }
        })
    }

    private fun updateCartButton() {
        if (::restaurantDetailModel.isInitialized && restaurantDetailModel.normal_cart != null) {
            AppConstants.CART_RESTAURANT = restaurantDetailModel.normal_cart!!.restaurant_name
            AppConstants.CART_COUNT = restaurantDetailModel.normal_cart!!.quantity
            (activity as MainActivity).updateCartButton()
        }
    }


}