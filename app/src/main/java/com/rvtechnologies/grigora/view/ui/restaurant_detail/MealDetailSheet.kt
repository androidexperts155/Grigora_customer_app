package com.rvtechnologies.grigora.view.ui.restaurant_detail

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.rvtechnologies.grigora.model.models.ItemSubCategory
  import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.FragmentMealDetailSheetBinding
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.ItemAdOnsAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel
import kotlinx.android.synthetic.main.fragment_meal_detail_sheet.*
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


class MealDetailSheet(var mealItem: RestaurantDetailNewModel.MealItem) :
    BottomSheetDialogFragment(),
    IRecyclerItemClick {
    var bi: FragmentMealDetailSheetBinding? = null
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    var list = ArrayList<RestaurantDetailNewModel.MealItem.ItemCategory>()
    private var selectedItemCategoriesList = ArrayList<ItemSubCategory?>()

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

        bottomSheetBehavior!!.peekHeight = (displayMetrics.heightPixels)


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
                    dismiss();
                }
            }
        })

        list.addAll(mealItem.item_categories)
        bi!!.rvOptions.adapter = ItemAdOnsAdapter(list, this)

        CommonUtils.loadImage(bi!!.imgMeal,mealItem.image)
        bi!!.tvName.text=mealItem.name
        bi!!.tvDesc.text=mealItem.description
        bi!!.tvRating.text=mealItem.avg_ratings.toString()
        bi!!.tvTime.text=getString(R.string.preparein) + " " + mealItem.approx_prep_time + " min"

        return bottomSheet
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
        if (item is ItemSubCategory) {
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
        }
    }


}
