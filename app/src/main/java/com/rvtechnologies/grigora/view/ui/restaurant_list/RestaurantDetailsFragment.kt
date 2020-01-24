package com.rvtechnologies.grigora.view.ui.restaurant_list

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.RestaurantDetailsFragmentBinding
import com.rvtechnologies.grigora.model.RestaurantDetailModel
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.model.models.CommonResponseModel
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
import kotlinx.android.synthetic.main.temp_about_us.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RestaurantDetailsFragment : Fragment(), OnItemClickListener, QuantityClicks,
    QuantityClicksDialog,
    IRecyclerItemClick {
    private val cartItemList = ArrayList<CartDetail>()
    lateinit var menuItemModel: RestaurantDetailModel.AllData.Item


    private lateinit var fragmentRestaurantsDetailsBinding: RestaurantDetailsFragmentBinding

    companion object {
        fun newInstance() = RestaurantDetailsFragment()
    }

    private lateinit var viewModel: RestaurantDetailsViewModel
    private val mealsAndCuisinesList = ArrayList<RestaurantDetailModel.AllData>()
    private val popularList = ArrayList<RestaurantDetailModel.AllData.Item>()
    private val previousList = ArrayList<RestaurantDetailModel.AllData.Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RestaurantDetailsViewModel::class.java)

        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

        viewModel.restaurantDetail.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                mealsAndCuisinesList.clear()
                if (response.status!!) {
                    var data = response.data as RestaurantDetailModel
                    if (data.popluarItems.size == 0) {
                        li_popular.visibility = View.GONE
                    } else {
                        popularList.addAll(data.popluarItems)
                    }

                    if (data.previousOrderedItems.size == 0) {
                        li_previous.visibility = View.GONE
                    } else {
                        previousList.addAll(data.previousOrderedItems)
                    }

                    if (data.full_time.equals("1")) {
                        tv_tt.text = "Open 24 hours"
                        tv_time.visibility = View.GONE
                    } else
                        tv_time.text = getFormattedTimeOrDate(
                            data.opening_time,
                            "HH:mm:ss",
                            "hh:mm:ss"
                        ) + " to " + getFormattedTimeOrDate(
                            data.closing_time,
                            "HH:mm:ss",
                            "hh:mm:ss"
                        )

                    tv_rating.text = data.total_rating
                    tv_reviews.text = data.total_review
                    tv_restaurantname.text = data.restaurant_name
                    tv_cuisines.text = data.cuisines
                    tv_deliver.text = "Delivers in " + data.estimated_preparing_time + " min"

                    mealsAndCuisinesList.addAll((data.allData))
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
                    view?.findNavController()
                        ?.navigate(
                            R.id.action_restaurantDetailsFragment_to_menuItemDetailsFragment,
                            bundle
                        )
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

        viewModel.getRestaurantsDetails(
            CommonUtils.getPrefValue(
                context,
                PrefConstants.TOKEN
            ),
            arguments?.get(AppConstants.RESTAURANT_ID).toString(),
            ""
        )
    }

    fun getFormattedTimeOrDate(data: String, patternFrom: String, patternTo: String): String {
        var d: Date? = null
        val sdf = SimpleDateFormat(patternFrom)
        try {
            d = sdf.parse(data)
        } catch (ex: ParseException) {
            Log.e("exp", "" + ex.message)
        }
        sdf.applyPattern(patternTo)
        return "" + sdf.format(d)
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

        for (i in 0 until tab_top.tabCount) {
            //noinspection ConstantConditions
            var tv = layoutInflater?.inflate(R.layout.tab_textview, null) as TextView
            tv.text = tab_top.getTabAt(i)?.text

            if (tab_top.getTabAt(i)?.isSelected!!) {
                tv.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            } else {
                if (CommonUtils.isDarkMode())
                    tv.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                else
                    tv.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            }
            tab_top.getTabAt(i)?.setCustomView(tv)
        }

        val token = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

        //      -1 states that it is popular
        rc_popular.adapter = RestaurantItemAdapter(popularList, this, this, -1)
        rc_popular.isNestedScrollingEnabled = false

//      -2 states that it is previous ordered
        rc_previous.adapter = RestaurantItemAdapter(previousList, this, this, -2)
        rc_previous.isNestedScrollingEnabled = false

        rc_items.adapter = RestaurantDetailAdapter(mealsAndCuisinesList, this, this)
        rc_items.isNestedScrollingEnabled = false

//        viewModel.getRestaurantsDetails(
//            CommonUtils.getPrefValue(
//                context,
//                PrefConstants.TOKEN
//            ), CommonUtils.getPrefValue(
//                context,
//                PrefConstants.ID
//            ),""
//        )
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).lockDrawer(true)
        }
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
        if (mealsAndCuisinesList[position].items[position2].itemCategories.size!! > 0) {
//                have add ons
            if (mealsAndCuisinesList[position].items[position2].cart_quantity.toInt() > 0) {
//                already have added before, call api and get what is added
                showItems(mealsAndCuisinesList[position].items[position2])
            } else {
//                show item details screen
                val bundle =
                    bundleOf(AppConstants.MENU_ITEM_MODEL to mealsAndCuisinesList[position])
                view?.findNavController()
                    ?.navigate(
                        R.id.action_restaurantDetailsFragment_to_menuItemDetailsFragment,
                        bundle
                    )
            }
        } else {
//                don't have add ons, simply add
            mealsAndCuisinesList[position].items[position2].cart_quantity =
                (mealsAndCuisinesList[position].items[position2].cart_quantity.toInt() + 1).toString()

            viewModel.addItemToCart(
                mealsAndCuisinesList[position].items[position2].restaurantId.toString()!!,
                mealsAndCuisinesList[position].items[position2].id.toString(),
                mealsAndCuisinesList[position].items[position2].price.toString(),
                "1"
            )

            rc_items.adapter?.notifyDataSetChanged()
        }
    }

    override fun minus(position: Int, position2: Int) {

        if (mealsAndCuisinesList[position].items[position2].itemCategories?.size!! > 0) {
//                have add ons
            if (mealsAndCuisinesList[position].items[position2].cart_quantity.toInt() > 0) {
//                already have added before, call api and get what is added
                showItems(mealsAndCuisinesList[position].items[position2])
            } else {
//                show item details screen
                val bundle =
                    bundleOf(AppConstants.MENU_ITEM_MODEL to mealsAndCuisinesList[position])
                view?.findNavController()
                    ?.navigate(
                        R.id.action_restaurantDetailsFragment_to_menuItemDetailsFragment,
                        bundle
                    )
            }
        } else {
//                don't have add ons, simply add
            mealsAndCuisinesList[position].items[position2].cart_quantity =
                (mealsAndCuisinesList[position].items[position2].cart_quantity.toInt() - 1).toString()


            viewModel.addItemToCart(
                mealsAndCuisinesList[position].items[position2].restaurantId.toString()!!,
                mealsAndCuisinesList[position].items[position2].id.toString(),
                mealsAndCuisinesList[position].items[position2].price.toString(),
                "-1"
            )

            rc_items.adapter?.notifyDataSetChanged()
        }
    }

    fun showItems(model: RestaurantDetailModel.AllData.Item) {
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
        if (item is RestaurantDetailModel.AllData.Item) {
            menuItemModel = item
            if (item.itemCategories!!.isNotEmpty()) {
                val bundle = bundleOf(AppConstants.MENU_ITEM_MODEL to item)
                view?.findNavController()
                    ?.navigate(
                        R.id.action_restaurantDetailsFragment_to_menuItemDetailsFragment,
                        bundle
                    )
            }
        }
    }

}
