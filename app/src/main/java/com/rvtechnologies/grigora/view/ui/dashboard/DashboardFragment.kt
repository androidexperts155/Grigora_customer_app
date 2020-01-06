package com.rvtechnologies.grigora.view.ui.dashboard

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.*
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.CategoryAdapter
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.PopularRestaurantAdapter
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.QuickAdapter
import com.rvtechnologies.grigora.view_model.DashboardViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_login.view.*
import kotlinx.android.synthetic.main.dashboard_fragment.*


class DashboardFragment : Fragment(), IRecyclerItemClick, OnItemClickListener {
    override fun onItemClick(item: Any) {
        if (item is RestaurantModel) {
            val bundle = bundleOf(AppConstants.RESTAURANT_MODEL to item)
            view?.findNavController()
                ?.navigate(
                    R.id.action_dashboardFragment_to_restaurantDetailsFragment,
                    bundle
                )
        }
        if (item is CategoryModel) {
            for (catItem in categoryList) {
                if (catItem.id == item.id) {
                    catItem.checked = true
                    viewModel.id.value = item.id
                    rec_categories.adapter?.notifyItemChanged(categoryList.indexOf(catItem))
                    getMenuItemByCategory()
                } else if (catItem.checked) {
                    catItem.checked = false
                    rec_categories.adapter?.notifyItemChanged(categoryList.indexOf(catItem))
                }
            }
        }
        if (item is Int) {
            if (item == 0) {
                showLoginAlert(activity as MainActivity)
            }
        }
        if (item is MenuItemModel) {
            val bundle = bundleOf(AppConstants.MENU_ITEM_MODEL to item)

            view?.findNavController()
                ?.navigate(
                    R.id.action_navigationRestaurants_to_menuItemDetailsFragment,
                    bundle
                )
        }
        if (item is HashMap<*, *>) {
            viewModel.click(
                item["check"] as Int,
                item["itemId"] as Int,
                CommonUtils.getPrefValue(activity!!, PrefConstants.TOKEN)
            )
        }

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
                        R.id.action_dashboardFragment_to_loginFragment
                    )
            }
            dialogView.btnLater.setOnClickListener {
                alertDialog?.dismiss()
            }
            alertDialog = dialogBuilder?.create()
            alertDialog?.show()
        }
    }

    companion object {
        fun newInstance() = DashboardFragment()
    }

    private lateinit var viewModel: DashboardViewModel
    private val restaurantPopularList = ArrayList<RestaurantModel>()
    private val categoryList = ArrayList<CategoryModel>()
    private val quickList = ArrayList<MenuItemModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            viewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)

            viewModel.lat.value =
                CommonUtils.getPrefValue(context, PrefConstants.LATITUDE).toDouble()
            viewModel.lng.value =
                CommonUtils.getPrefValue(context, PrefConstants.LONGITUDE).toDouble()
            viewModel.user_id.value =
                CommonUtils.getPrefValue(context, PrefConstants.ID)

            viewModel.menuListByCategoryRes.observe(this,
                Observer { response ->
                    quickList.clear()
                    if (response is CommonResponseModel<*>) {
                        if (response.status!!) {
                            quickList.addAll(response.data as Collection<MenuItemModel>)
                            rec_quick.adapter?.notifyDataSetChanged()
                        }
                    } else {
                        CommonUtils.showMessage(parentView, response.toString())
                    }

//                    error.visibility = if (menuList.isEmpty()) View.VISIBLE else GONE
                    parentView.visibility = if (!quickList.isEmpty()) View.VISIBLE else VISIBLE
                })

            viewModel.categoryListRes.observe(this, Observer<Any> { response ->
                if (response is CommonResponseModel<*>) {
                    if (response.status!!) {
                        categoryList.clear()
                        if (response.data is Collection<*>)
                            categoryList.addAll(response.data as Collection<CategoryModel>)
                        rec_categories.adapter = CategoryAdapter(categoryList, this)
                    }
                } else {
                    CommonUtils.showMessage(parentView, response.toString())
                }
            })
            viewModel.restaurantListRes.observe(this,
                Observer { response ->
                    restaurantPopularList.clear()
                    if (response is CommonResponseModel<*>) {
                        if (response.status!!) {
                            restaurantPopularList.addAll(response.data as Collection<RestaurantModel>)
                            rvPopularRestaurants?.adapter?.notifyDataSetChanged()
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
                        try {
                            CommonUtils.hideLoader()
                        } catch (e: Exception) {
                            Log.e("Exception: ", e.message)
                        }
                    }
                })
        } catch (e: Exception) {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dashboard_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPopularRestaurants?.isNestedScrollingEnabled = false
        rvPopularRestaurants?.adapter = PopularRestaurantAdapter(restaurantPopularList, this)

        rec_quick?.isNestedScrollingEnabled = false
        rec_quick?.adapter = QuickAdapter(quickList, this)

        bt_location.setOnClickListener {
            CommonUtils.savePrefs(
                activity,
                PrefConstants.TEMP_LATITUDE,
                CommonUtils.getPrefValue(activity, PrefConstants.LATITUDE)
            )
            CommonUtils.savePrefs(
                activity,
                PrefConstants.TEMP_LONGITUDE,
                CommonUtils.getPrefValue(activity, PrefConstants.LONGITUDE)
            )
            CommonUtils.savePrefs(
                activity,
                PrefConstants.TEMP_ADDRESS,
                CommonUtils.getPrefValue(activity, PrefConstants.ADDRESS)
            )


            CommonUtils.savePrefs(activity, PrefConstants.LATITUDE, "")
            CommonUtils.savePrefs(activity, PrefConstants.LONGITUDE, "")
            CommonUtils.savePrefs(activity, PrefConstants.ADDRESS, "")


//            Navigation.findNavController(this, R.id.main_nav_fragment)
//                .popBackStack(R.id.nav_graph_xml, true)

            Navigation.findNavController(activity as MainActivity, R.id.main_nav_fragment)
                .navigate(R.id.selectLocationFragment)
        }

    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.VISIBLE
            (activity as MainActivity).img_menu.visibility = View.VISIBLE
            (activity as MainActivity).img_back.visibility = View.GONE
            (activity as MainActivity).updateLocation()
            (activity as MainActivity).lockDrawer(false)

        }
        getCategories()
        getPopularRestaurants()
    }

    private fun getMenuItemByCategory() {
        viewModel.getMenuItemByCategory()
    }

    private fun getPopularRestaurants() {
        viewModel.getPopularRestaurants(
//            CommonUtils.getPrefValue(
//                context,
//                PrefConstants.TOKEN
//            ),
            CommonUtils.getPrefValue(activity, PrefConstants.ID)

        )
    }

    private fun getCategories() {
        viewModel.getAllCategories()
    }

    override fun onPause() {
        super.onPause()
        viewModel.menuListByCategoryRes.removeObservers(context as AppCompatActivity)
        viewModel.categoryListRes.removeObservers(context as AppCompatActivity)
        viewModel.restaurantListRes.removeObservers(context as AppCompatActivity)
    }

//    private fun runLayoutAnimation(recyclerView: ViewGroup) {
//        val context = recyclerView.context
//        val controller =
//            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
//
//        recyclerView.layoutAnimation = controller
////        recyclerView.adapter!!.notifyDataSetChanged()
//        recyclerView.scheduleLayoutAnimation()
//    }

}
