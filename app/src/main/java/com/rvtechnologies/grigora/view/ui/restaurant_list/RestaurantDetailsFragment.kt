package com.rvtechnologies.grigora.view.ui.restaurant_list

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.RestaurantDetailsFragmentBinding
import com.rvtechnologies.grigora.model.RestaurantDetailModel
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.ItemsCartAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantDetailAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantItemAdapter
import com.rvtechnologies.grigora.view_model.RestaurantDetailsViewModel
import kotlinx.android.synthetic.main.alert_login.view.*
import kotlinx.android.synthetic.main.existing_cart_dialog.view.*
import kotlinx.android.synthetic.main.restaurant_details_fragment.*
import kotlinx.android.synthetic.main.restaurant_details_fragment.parentView
import kotlin.collections.ArrayList

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RestaurantDetailsViewModel::class.java)

        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

        viewModel.restaurantDetail.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                mealsAndCuisinesList.clear()
                filteredMealsAndCuisinesList.clear()
                if (response.status!!) {
                    var data = response.data as RestaurantDetailModel
                    if (data.popluarItems.size == 0) {
                        li_popular.visibility = View.GONE
                    } else {
                        popularList.addAll(data.popluarItems)
                        rc_popular.adapter!!.notifyDataSetChanged()
                        tv_popular_count.text = data.popluarItems.size.toString()
                    }

                    if (data.previousOrderedItems.size == 0) {
                        li_previous.visibility = View.GONE
                    } else {
                        previousList.addAll(data.previousOrderedItems)
                        tv_pre_count.text = data.previousOrderedItems.size.toString()
                        rc_previous.adapter!!.notifyDataSetChanged()
                    }

                    if (data.full_time.equals("1")) {
                        tv_tt.text = "Open 24 hours"
                        tv_time.visibility = View.GONE
                    } else
                        tv_time.text = CommonUtils.getFormattedTimeOrDate(
                            data.opening_time,
                            "HH:mm:ss",
                            "hh:mm aa"
                        ) + " to " + CommonUtils.getFormattedTimeOrDate(
                            data.closing_time,
                            "HH:mm:ss",
                            "hh:mm aa"
                        )

                    tv_rating.text = data.total_rating
                    tv_reviews.text = data.total_review
                    tv_restaurantname.text = data.restaurant_name
                    tv_cuisines.text = data.cuisines
                    tv_deliver.text = "Delivers in " + data.estimated_preparing_time + " min"

                    mealsAndCuisinesList.addAll((data.allData))
                    filteredMealsAndCuisinesList.addAll(mealsAndCuisinesList)
                    rc_items?.adapter?.notifyDataSetChanged()
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
                        ), CommonUtils.getPrefValue(
                            context,
                            PrefConstants.ID
                        ), ""
                    )
                }
                alertDialog = dialogBuilder?.create()
                alertDialog?.show()
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
        rc_previous.adapter = RestaurantItemAdapter(previousList, this, this, -2)
        rc_previous.isNestedScrollingEnabled = false

        rc_items.adapter = RestaurantDetailAdapter(filteredMealsAndCuisinesList, this, this)
        rc_items.isNestedScrollingEnabled = false

        filterVegNonVeg()
        initSearchView()
        manageSwitch()
    }

    fun manageSwitch() {
        tv_delivery.setOnClickListener {
            tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_delivery.setBackgroundResource(R.drawable.delivery_sel)

            tv_pickup.setBackgroundResource(R.drawable.pickup_de_sel)

            if (CommonUtils.isDarkMode()) {
                tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            } else
                tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))


        }

        tv_pickup.setOnClickListener {
            tv_pickup.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            tv_pickup.setBackgroundResource(R.drawable.pickup_sel)

            tv_delivery.setBackgroundResource(R.drawable.delivery_de_sel)

            if (CommonUtils.isDarkMode()) {
                tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            } else
                tv_delivery.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
        }

    }

    private fun filterVegNonVeg() {
        rg_veg_nonveg.setOnCheckedChangeListener { radioGroup, i ->
            var filter = ""
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

            filteredMealsAndCuisinesList.clear()
//            filteredMealsAndCuisinesList =  mealsAndCuisinesList.clone() as ArrayList<RestaurantDetailModel.AllData>

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
    }

    fun back() {
        view?.findNavController()?.popBackStack()
    }

    fun like(view: View) {
        CommonUtils.doBounceAnimation(view)
    }

    fun toReviews() {
        val bundle = bundleOf(AppConstants.RESTAURANT_ID to viewModel.id.value)
        view?.findNavController()
            ?.navigate(R.id.action_restaurantDetailsFragment_to_reviewsFragment, bundle)
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
                view?.findNavController()
                    ?.navigate(
                        R.id.action_restaurantDetailsFragment_to_loginFragment
                    )
            }
            dialogView.btnLater.setOnClickListener {
                alertDialog?.dismiss()
            }
            alertDialog = dialogBuilder?.create()
            alertDialog?.show()
        }
    }

    override fun add(position: Int, position2: Int) {
//     position=   -2 previous ordered, -1 popular

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
                popularList[position2].item_count_in_cart =
                    (popularList[position2].item_count_in_cart!! + 1)

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
                previousList[position2].item_count_in_cart =
                    (previousList[position2].item_count_in_cart!! + 1)

                viewModel.addItemToCart(
                    previousList[position2].restaurantId.toString()!!,
                    previousList[position2].id.toString(),
                    previousList[position2].price.toString(),
                    "1"
                )

                rc_previous.adapter?.notifyDataSetChanged()
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
                filteredMealsAndCuisinesList[position].items[position2].item_count_in_cart =
                    (filteredMealsAndCuisinesList[position].items[position2].item_count_in_cart!! + 1)

                viewModel.addItemToCart(
                    filteredMealsAndCuisinesList[position].items[position2].restaurantId.toString()!!,
                    filteredMealsAndCuisinesList[position].items[position2].id.toString(),
                    filteredMealsAndCuisinesList[position].items[position2].price.toString(),
                    "1"
                )

                rc_items.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun minus(position: Int, position2: Int) {
//     position=   -2 previous ordered, -1 popular

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
                popularList[position2].item_count_in_cart =
                    (popularList[position2].item_count_in_cart!! - 1)


                viewModel.addItemToCart(
                    popularList[position2].restaurantId.toString()!!,
                    popularList[position2].id.toString(),
                    popularList[position2].price.toString(),
                    "-1"
                )

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
                previousList[position2].item_count_in_cart =
                    (previousList[position2].item_count_in_cart!! - 1)


                viewModel.addItemToCart(
                    previousList[position2].restaurantId.toString()!!,
                    previousList[position2].id.toString(),
                    previousList[position2].price.toString(),
                    "-1"
                )

                rc_previous.adapter?.notifyDataSetChanged()
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
                filteredMealsAndCuisinesList[position].items[position2].item_count_in_cart =
                    (filteredMealsAndCuisinesList[position].items[position2].item_count_in_cart!! - 1)


                viewModel.addItemToCart(
                    filteredMealsAndCuisinesList[position].items[position2].restaurantId.toString()!!,
                    filteredMealsAndCuisinesList[position].items[position2].id.toString(),
                    filteredMealsAndCuisinesList[position].items[position2].price.toString(),
                    "-1"
                )

                rc_items.adapter?.notifyDataSetChanged()
            }
        }
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
            if (item.itemCategories!!.isNotEmpty()) {
                val bundle = bundleOf(AppConstants.MENU_ITEM_MODEL to item)
                iRecyclerItemClick.onItemClick(bundle)
            }
        }
    }

    fun initSearchView() {
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
}


