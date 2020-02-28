package com.rvtechnologies.grigora.view.ui.restaurant_list

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.MenuItemDetailsFragmentBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.ItemCategory
import com.rvtechnologies.grigora.model.models.ItemSubCategory
import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.ItemCategoryAdapter
import com.rvtechnologies.grigora.view_model.MenuItemDetailsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_login.view.*
import kotlinx.android.synthetic.main.menu_item_details_fragment.*


class MenuItemDetailsFragment : Fragment(), IRecyclerItemClick {

    override fun onItemClick(item: Any) {
        if (item is ItemSubCategory) {
            if (item.checked) {
                selectedItemCategoriesList.add(item)
            } else {
                selectedItemCategoriesList.remove(item)
            }
            viewModel.selectedChoices.value = selectedItemCategoriesList
            menuItemDetailsFragmentBinding.menuItemViewModel = viewModel
            viewModel.refresh()
        }
    }

    companion object {
        fun newInstance() = MenuItemDetailsFragment()
    }

    private var itemCategoriesList = ArrayList<ItemCategory>()
    private var selectedItemCategoriesList = ArrayList<ItemSubCategory?>()

    private lateinit var viewModel: MenuItemDetailsViewModel
    private lateinit var menuItemDetailsFragmentBinding: MenuItemDetailsFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MenuItemDetailsViewModel::class.java)
        if (arguments != null) {
            var menuItem = arguments?.get(AppConstants.MENU_ITEM_MODEL) as MenuItemModel
            if (menuItem.isForGroupCart) {
                viewModel.cartId.value = menuItem.cartId
            }

            menuItem.offPercentage = menuItem.restaurant_offer.toString().plus("%")

            viewModel.menuItem.value = menuItem
            viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
            viewModel.refresh()
        }

        viewModel.itemCount.observe(this, Observer {
            menuItemDetailsFragmentBinding.menuItemViewModel = viewModel
            tv_count.text = it
        })
        viewModel.itemCount.value = "1"
        viewModel.response.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                CommonUtils.showMessage(parentView, response.message!!)
                view?.findNavController()?.popBackStack()

            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }


        })
        viewModel.itemCategories.observe(this, Observer { itemCategoriesListRes ->
            itemCategoriesList.clear()
            itemCategoriesList.addAll(itemCategoriesListRes as Collection<ItemCategory>)
            rvOptions.adapter = ItemCategoryAdapter(itemCategoriesList, this)
//            txtChoices.visibility = if (itemCategoriesList.isEmpty()) GONE else VISIBLE
        })
        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                context?.let { it1 -> CommonUtils.showLoader(it1, getString(R.string.loading)) }
            } else {
                CommonUtils.hideLoader()
            }
        })

        viewModel.price.observe(this, Observer {
            btn_add.text = getString(R.string.add) + " (₦ " + it + ")"
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menuItemDetailsFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.menu_item_details_fragment, container, false)

        menuItemDetailsFragmentBinding.menuItemViewModel = viewModel
        menuItemDetailsFragmentBinding.menuItemView = this
        return menuItemDetailsFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCategoryItems()

    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle(viewModel.menuItem.value!!.restaurantName)
            (activity as MainActivity).lockDrawer(true)
            (activity as MainActivity).showBottomNavigation(0)
        }
        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

    }

    fun back() {
        activity?.onBackPressed()
    }

}

