package com.rvtechnologies.grigora.view.ui.restaurant_list

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.RestaurantDetailsFragmentBinding
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.model.models.RestaurantModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.ItemsCartAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.RestaurantItemAdapter
import com.rvtechnologies.grigora.view_model.RestaurantDetailsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_login.view.*
import kotlinx.android.synthetic.main.existing_cart_dialog.view.*
import kotlinx.android.synthetic.main.restaurant_details_fragment.*

class RestaurantDetailsFragment : Fragment(), OnItemClickListener, QuantityClicks,
    QuantityClicksDialog,
    IRecyclerItemClick {
    private val cartItemList = ArrayList<CartDetail>()
    lateinit var menuItemModel: MenuItemModel

    override fun onItemClick(item: Any) {
        if (item is MenuItemModel) {
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

    private lateinit var fragmentRestaurantsDetailsBinding: RestaurantDetailsFragmentBinding

    companion object {
        fun newInstance() = RestaurantDetailsFragment()
    }

    private lateinit var viewModel: RestaurantDetailsViewModel
    private val menuItemList = ArrayList<MenuItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RestaurantDetailsViewModel::class.java)

        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)


        viewModel.restaurantItemList.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                menuItemList.clear()
                if (response.status!!) {
                    menuItemList.addAll(response.data as Collection<MenuItemModel>)
                    rvRestaurantItems?.adapter?.notifyDataSetChanged()
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
                        )
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
        if (arguments != null) {
            val restaurant = arguments?.get(AppConstants.RESTAURANT_MODEL) as RestaurantModel
            restaurant.avgRatingsString = restaurant.avgRatings.toString()
            fragmentRestaurantsDetailsBinding.restaurantModel = restaurant
            viewModel.id.value = restaurant.id


            val token = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

            if (token.isBlank())
                fragmentRestaurantsDetailsBinding.tglLike.background =
                    ContextCompat.getDrawable(context!!, R.drawable.ic_like_default)
            else
                fragmentRestaurantsDetailsBinding.tglLike.background =
                    ContextCompat.getDrawable(context!!, R.drawable.like_button)



            fragmentRestaurantsDetailsBinding.tglLike.setOnClickListener {
                if (token.isBlank()) {
                    showLoginAlert(activity as MainActivity)
                } else {
                    like(fragmentRestaurantsDetailsBinding.tglLike)
                    var check = 1
                    if (!fragmentRestaurantsDetailsBinding.tglLike.isChecked)
                        check = 0
                    viewModel.click(check)
                }
            }
        }
        rvRestaurantItems.adapter = RestaurantItemAdapter(menuItemList, this, this)
        rvRestaurantItems.isNestedScrollingEnabled = false

        viewModel.getRestaurantsDetails(
            CommonUtils.getPrefValue(
                context,
                PrefConstants.TOKEN
            ), CommonUtils.getPrefValue(
                context,
                PrefConstants.ID
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.VISIBLE
            (activity as MainActivity).lockDrawer(true)
        }

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

    override fun add(position: Int) {
        if (menuItemList[position].itemCategories?.size!! > 0) {
//                have add ons
            if (menuItemList[position].item_count_in_cart!! > 0) {
//                already have added before, call api and get what is added
                showItems(menuItemList[position])
            } else {
//                show item details screen
                val bundle = bundleOf(AppConstants.MENU_ITEM_MODEL to menuItemList[position])
                view?.findNavController()
                    ?.navigate(
                        R.id.action_restaurantDetailsFragment_to_menuItemDetailsFragment,
                        bundle
                    )
            }
        } else {
//                don't have add ons, simply add
            menuItemList[position].item_count_in_cart =
                menuItemList[position].item_count_in_cart!! + 1

            viewModel.addItemToCart(
                menuItemList[position].restaurantId.toString()!!,
                menuItemList[position].id.toString(),
                menuItemList[position].price!!,
                "1"
            )

            rvRestaurantItems.adapter?.notifyDataSetChanged()
        }
    }

    override fun minus(position: Int) {
        if (menuItemList[position].itemCategories?.size!! > 0) {
//                have add ons
            if (menuItemList[position].item_count_in_cart!! > 0) {
//                already have added before, call api and get what is added
                showItems(menuItemList[position])
            } else {
//                show item details screen
                val bundle = bundleOf(AppConstants.MENU_ITEM_MODEL to menuItemList[position])
                view?.findNavController()
                    ?.navigate(
                        R.id.action_restaurantDetailsFragment_to_menuItemDetailsFragment,
                        bundle
                    )
            }
        } else {
//                don't have add ons, simply add
            menuItemList[position].item_count_in_cart =
                menuItemList[position].item_count_in_cart!! - 1

            viewModel.addItemToCart(
                menuItemList[position].restaurantId.toString()!!,
                menuItemList[position].id.toString(),
                menuItemList[position].price!!,
                "-1"
            )

            rvRestaurantItems.adapter?.notifyDataSetChanged()
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

}
