package com.rvtechnologies.grigora.view.ui.trending_meals

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.AddCartModel
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.model.models.TrendingMealsModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicks
import com.rvtechnologies.grigora.view.ui.restaurant_list.QuantityClicksDialog
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.ItemsCartAdapter
import com.rvtechnologies.grigora.view.ui.trending_meals.adapter.TrendingItemAdapter
import com.rvtechnologies.grigora.view_model.TrendingMealsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.existing_cart_dialog.view.*
import kotlinx.android.synthetic.main.trending_meals_fragment.*

class TrendingMealsFragment : Fragment(), OnItemClickListener, QuantityClicks, IRecyclerItemClick,
    QuantityClicksDialog {
    lateinit var trendingModel: TrendingMealsModel
    private val cartItemList = ArrayList<CartDetail>()
    lateinit var menuItemModel: MenuItemModel
    private var cartId = ""

    companion object {
        fun newInstance() = TrendingMealsFragment()
    }

    private lateinit var viewModel: TrendingMealsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TrendingMealsViewModel::class.java)
        viewModel.latitude.value = CommonUtils.getPrefValue(context!!, PrefConstants.LATITUDE)
        viewModel.longitude.value = CommonUtils.getPrefValue(context!!, PrefConstants.LONGITUDE)
        viewModel.token.value = CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                context?.let { it1 -> CommonUtils.showLoader(it1, "") }
            } else {
                CommonUtils.hideLoader()
            }
        })

        viewModel.trendingResponse.observe(this, Observer { res ->

            if (res is CommonResponseModel<*>) {
                if (res.status!!) {
                    trendingModel = res.data as TrendingMealsModel
                    rc_trending.adapter = TrendingItemAdapter(trendingModel.trending, this, this)
                } else {

                }
            } else {

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
                        this, this
                    )
                dialogView.bt_add_new.setOnClickListener {
                    alertDialog?.dismiss()
                    val bundle = bundleOf(AppConstants.MENU_ITEM_MODEL to menuItemModel)
                    moveToDetail(bundle)
                }

                dialogView.img_close.setOnClickListener {
                    alertDialog?.dismiss()

                    viewModel.trendingMeals()
                }
                alertDialog = dialogBuilder?.create()
                alertDialog?.show()
            }
        })

        viewModel.addCartRes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                var data = response.data as AddCartModel
                cartId = data.cartId.toString()
                if (data.quantity > 0)
                    AppConstants.CART_COUNT = data.quantity
                (activity as MainActivity).updateCartButton()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.trending_meals_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).lockDrawer(true)
        (activity as MainActivity).backTitle(getString(R.string.trending))
        (activity as MainActivity).img_back.visibility = View.GONE
        (activity as MainActivity).showBottomNavigation(2)

        viewModel.trendingMeals()
    }

    private fun showItems(model: MenuItemModel) {
        menuItemModel=model
        viewModel.getCartItems(viewModel.token.value!!, model.id.toString())
    }

    override fun onItemClick(item: Any) {
        if (item is MenuItemModel) {
            if (item.itemCategories!!.isNotEmpty()) {
                menuItemModel = item
                val bundle = bundleOf(AppConstants.MENU_ITEM_MODEL to item)
                moveToDetail(bundle)
            }
        }
    }

    override fun add(position: Int, position2: Int) {
        if (trendingModel.trending[position2].itemCategories?.size!! > 0) {
//                have add ons
            if (trendingModel.trending[position2].item_count_in_cart!! > 0) {
//                already have added before, call api and get what is added
                showItems(trendingModel.trending[position2])
            } else {
//                show item details screen
                menuItemModel = trendingModel.trending[position2]
                val bundle =
                    bundleOf(AppConstants.MENU_ITEM_MODEL to trendingModel.trending[position2])
                moveToDetail(bundle)
            }
        } else {
//                don't have add ons, simply add
            trendingModel.trending[position2].item_count_in_cart =
                trendingModel.trending[position2].item_count_in_cart!! + 1

            viewModel.addItemToCart(
                trendingModel.trending[position2].restaurantId.toString()!!,
                trendingModel.trending[position2].id.toString(),
                trendingModel.trending[position2].price.toString(),
                "1"
            )

            rc_trending.adapter?.notifyDataSetChanged()
        }

    }

    override fun minus(position: Int, position2: Int) {
        if (trendingModel.trending[position2].itemCategories?.size!! > 0) {
//                have add ons
            if (trendingModel.trending[position2].item_count_in_cart!! > 0) {
//                already have added before, call api and get what is added
                showItems(trendingModel.trending[position2])
            } else {
//                show item details screen
                menuItemModel = trendingModel.trending[position2]
                val bundle =
                    bundleOf(AppConstants.MENU_ITEM_MODEL to trendingModel.trending[position2])
                moveToDetail(bundle)
            }
        } else {
//                don't have add ons, simply add
            trendingModel.trending[position2].item_count_in_cart =
                trendingModel.trending[position2].item_count_in_cart!! - 1

            viewModel.addItemToCart(
                trendingModel.trending[position2].restaurantId.toString()!!,
                trendingModel.trending[position2].id.toString(),
                trendingModel.trending[position2].price.toString(),
                "1"
            )

            rc_trending.adapter?.notifyDataSetChanged()
        }

    }

    override fun dialogAdd(position: Int) {
        viewModel.updateCartQty(
            viewModel.token.value!!,
            cartItemList[position].id!!,
            cartItemList[position].cartId!!,
            "1"
        )    }

    override fun dialogMinus(position: Int) {
//        hit sub api
        viewModel.updateCartQty(
            viewModel.token.value!!,
            cartItemList[position].id!!,
            cartItemList[position].cartId!!,
            "-1"
        )    }

    fun moveToDetail(bundle: Bundle) {
        view?.findNavController()
            ?.navigate(
                R.id.action_trendingMeals_to_menuItemDetailsFragment,
                bundle
            )

    }
}
