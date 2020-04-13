package com.rvtechnologies.grigora.view.ui.restaurant_detail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.MealsAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.FeaturedModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.MealsListModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import com.rvtechnologies.grigora.view_model.MealsListViewModel
import kotlinx.android.synthetic.main.meals_list_fragment.*

class MealsList : Fragment(), IRecyclerItemClick, MealDetailSheet.Refresh {

    companion object {
        fun newInstance() = MealsList()
    }

    lateinit var mealsListModel: MealsListModel

    private lateinit var viewModel: MealsListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.meals_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MealsListViewModel::class.java)

        var cartId =
            if (arguments?.containsKey(AppConstants.CART_ID)!!) arguments?.get(AppConstants.CART_ID)!!
                .toString() else ""
        viewModel.getMeals(
            arguments?.get(AppConstants.CUISINE_ID)!!.toString(),
            cartId,
            arguments?.get(AppConstants.FILTER_ID)!!.toString(),
            arguments?.get(AppConstants.RESTAURANT_ID)!!.toString()
        )

        viewModel?.isLoading?.observe(this, Observer { isLoading ->
            if (isLoading) {
                context?.let { it1 -> CommonUtils.showLoader(it1, getString(R.string.loading)) }
            } else {
                CommonUtils.hideLoader()
            }
        })

        viewModel.mealsist.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    mealsListModel = response.data as MealsListModel

                    var list = ArrayList<RestaurantDetailNewModel.MealItem>()
                    list.addAll(mealsListModel.items)
                    rc_meals.adapter = MealsAdapter(list, this)
                    updateCartButton()
                } else {
                    CommonUtils.showMessage(parent, response.message!!)
                }
            } else {
                CommonUtils.showMessage(parent, response.toString())
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(arguments?.get(AppConstants.CUISINE_NAME)!!.toString())
    }

    override fun onItemClick(item: Any) {
        if (item is RestaurantDetailNewModel.MealItem) {
            var cartId =
                if (arguments?.containsKey(AppConstants.CART_ID)!!) arguments?.get(AppConstants.CART_ID)!!
                    .toString() else ""

            var sheet = MealDetailSheet(item, cartId, this)
            sheet.show(childFragmentManager, "")
        }
    }

    override fun refresh(refresh: Boolean) {
        var cartId =
            if (arguments?.containsKey(AppConstants.CART_ID)!!) arguments?.get(AppConstants.CART_ID)!!
                .toString() else ""
        if (refresh) {
            viewModel.getMeals(
                arguments?.get(AppConstants.CUISINE_ID)!!.toString(),
                cartId,
                arguments?.get(AppConstants.FILTER_ID)!!.toString(),
                arguments?.get(AppConstants.RESTAURANT_ID)!!.toString()
            )
        }
    }

    private fun updateCartButton() {

        if (mealsListModel.cart != null) {
            AppConstants.CART_RESTAURANT = mealsListModel.items[0].restaurant_name
            AppConstants.CART_COUNT = mealsListModel.cart!!.quantity
            (activity as MainActivity).updateCartButton()
        }
    }

}
