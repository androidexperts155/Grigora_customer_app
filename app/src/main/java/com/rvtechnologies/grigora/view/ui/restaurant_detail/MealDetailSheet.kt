package com.rvtechnologies.grigora.view.ui.restaurant_detail

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
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
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.ItemRemovableAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.ShowSubAdOnModel
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.SubAdOnAdded
import com.rvtechnologies.grigora.view_model.MenuItemSheetViewModel

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
    var removableList = ArrayList<RestaurantDetailNewModel.MealItem.Removables>()
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
        setRemovables()
        setObservers()
        return bottomSheet
    }

    fun setRemovables() {
        if (mealItem.item_removeables!!.isNotEmpty()) {
            bi!!.liRemovables.visibility = View.VISIBLE
            bi!!.imgRemovable.setOnClickListener {
                if (bi!!.rvRemovables.visibility == View.VISIBLE) {
                    it.rotation = -90F
                    bi!!.rvRemovables.visibility = View.GONE
                } else {
                    bi!!.rvRemovables.visibility = View.VISIBLE
                    it.rotation = -0F
                }
            }

            removableList.addAll(mealItem.item_removeables)


            bi!!.rvRemovables.adapter = ItemRemovableAdapter(removableList, this)


        } else {
            bi!!.liRemovables.visibility = View.GONE
        }
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
                    Toast.makeText(context, response.message!!, Toast.LENGTH_SHORT).show()

                    refresh.refresh(true)
                    dismiss()
                    viewModel.destroy(activity as MainActivity)
                } else
                    Toast.makeText(context, response.message!!, Toast.LENGTH_SHORT).show()

            } else if (response != null) {
                Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show()
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
        when (item) {
            is RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory -> {
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
                    for (i in item.item_sub_sub_category) {
                        i.checked = false
                    }
                    selectedItemCategoriesList.remove(item)
                }
                viewModel.selectedChoices.value = selectedItemCategoriesList

                bi!!.menuItemViewModel = viewModel

                viewModel.refresh()
                bi!!.rvOptions.adapter?.notifyDataSetChanged()
            }
            is ShowSubAdOnModel -> {
                var subAdOnSheet = SubAdOnSheet(item.subCategory, this)
                subAdOnSheet.show(childFragmentManager, "")
            }
            is SubAdOnAdded -> {
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
            is Int -> {
                removableList[item].checked = !removableList[item].checked
                bi!!.rvRemovables.adapter?.notifyDataSetChanged()
                viewModel.removableList = removableList
            }
        }
    }

    interface Refresh {
        fun refresh(refresh: Boolean)
    }
}
