package com.rvtechnologies.grigora.view.ui.restaurant_detail

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.rvtechnologies.grigora.model.models.ItemSubCategory
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.FragmentMealDetailSheetBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.ItemAdOnsAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.ShowSubAdOnModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.SubAdOnAdded
import com.rvtechnologies.grigora.view_model.MenuItemDetailsViewModel
import com.rvtechnologies.grigora.view_model.MenuItemSheetViewModel
import kotlinx.android.synthetic.main.fragment_meal_detail_sheet.*
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


class MealDetailSheet(
    var mealItem: RestaurantDetailNewModel.MealItem,
    var cartId: String,
    var refresh: Refresh
) :
    BottomSheetDialogFragment(),
    IRecyclerItemClick {
    private lateinit var viewModel: MenuItemSheetViewModel

    var bi: FragmentMealDetailSheetBinding? = null
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    var list = ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory>()
    private var selectedItemCategoriesList =
        ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory?>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view =
            View.inflate(context, R.layout.fragment_meal_detail_sheet, null)

        bi = DataBindingUtil.bind(view)!!

        bottomSheet.setContentView(view);
        bottomSheetBehavior = BottomSheetBehavior.from((view.parent as View))

        var displayMetrics = DisplayMetrics()
        (activity as MainActivity).windowManager.defaultDisplay.getMetrics(
            displayMetrics
        )

        bottomSheetBehavior!!.peekHeight =
            (displayMetrics.heightPixels)


        bottomSheetBehavior!!.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
                }
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                    viewModel.destroy(activity as MainActivity)

                }
            }
        })

        list.addAll(mealItem.item_categories)
        bi!!.rvOptions.adapter = ItemAdOnsAdapter(list, this)

        CommonUtils.loadImage(bi!!.imgMeal, mealItem.image)
        bi!!.tvName.text = mealItem.name
        bi!!.tvDesc.text = mealItem.description
        bi!!.tvRating.text = mealItem.avg_ratings.toString()
        bi!!.tvTime.text = getString(R.string.preparein) + " " + mealItem.approx_prep_time + " min"
        bi!!.imgClose.setOnClickListener { close() }
        bi!!.btnAdd.setOnClickListener { viewModel.addItemToCart() }
        setObservers()
        return bottomSheet
    }

    fun setObservers() {
        viewModel =
            activity!!.let { ViewModelProviders.of(it).get(MenuItemSheetViewModel::class.java) }

        if (!cartId.isNullOrEmpty()) {
            viewModel.cartId.value = cartId
        }

        viewModel.menuItem.value = mealItem

        viewModel.itemCount.value = "1"

        viewModel.itemCount.observe(this, Observer {
            if (it != null) {
                bi!!.menuItemViewModel = viewModel
                bi!!.tvCount.text = it
                viewModel.refresh()
            }
        })

        viewModel.price.observe(this, Observer {
            if (it != null) {
                bi!!.btnAdd.text = getString(R.string.add) + " (₦ " + it + ")"
            }
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading != null)
                if (isLoading) {
                    context?.let { it1 -> CommonUtils.showLoader(it1, getString(R.string.loading)) }
                } else {
                    CommonUtils.hideLoader()
                }
        })

        viewModel.response.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    CommonUtils.showMessage(parent, response.message!!)
                    refresh.refresh(true)
                    dismiss()
                    viewModel.destroy(activity as MainActivity)
                } else
                    CommonUtils.showMessage(parent, response.message!!)
            } else if (response != null) {
                CommonUtils.showMessage(this.view, response.toString())
            }
        })
    }

    fun close() {
        dismiss()
        viewModel.destroy(activity as MainActivity)

    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

    }

    override fun onItemClick(item: Any) {
        if (item is RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory) {
            if (item.checked) {
                var add = true

                for (i in selectedItemCategoriesList) {
                    if (i?.id!! == item.id) {
                        add = false
                    }
                }

                if (add)
                    selectedItemCategoriesList.add(item)
            } else {
                selectedItemCategoriesList.remove(item)
            }
            viewModel.selectedChoices.value = selectedItemCategoriesList

            bi!!.menuItemViewModel = viewModel

            viewModel.refresh()
            bi!!.rvOptions.adapter?.notifyDataSetChanged()
        } else if (item is ShowSubAdOnModel) {
            var subAdOnSheet = SubAdOnSheet(item.subCategory, this)
            subAdOnSheet.show(childFragmentManager, "")
        } else if (item is SubAdOnAdded) {
            if (item.itemSubCategory.checked) {
                var add = true

                for (i in 0 until selectedItemCategoriesList.size) {
                    if (selectedItemCategoriesList[i]?.id!! == item.itemSubCategory.id) {
                        selectedItemCategoriesList[i]?.item_sub_sub_category?.clear()
                        selectedItemCategoriesList[i]?.item_sub_sub_category?.addAll(item.itemSubCategory.item_sub_sub_category)
                    }
                }


            }
            viewModel.selectedChoices.value = selectedItemCategoriesList

            bi!!.menuItemViewModel = viewModel

            viewModel.refresh()
            bi!!.rvOptions.adapter?.notifyDataSetChanged()
        }
    }

    interface Refresh {
        fun refresh(refresh: Boolean)
    }
}
