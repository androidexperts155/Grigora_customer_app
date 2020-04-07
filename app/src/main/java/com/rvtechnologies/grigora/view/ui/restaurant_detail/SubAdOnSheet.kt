package com.rvtechnologies.grigora.view.ui.restaurant_detail

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.FragmentSubAdOnSheetBinding
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.restaurant_detail.adapter.NextAdOnAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel

class SubAdOnSheet(var itemSubCategory: RestaurantDetailNewModel.MealItem.ItemCategory.ItemSubCategory,var iRecyclerItemClick: IRecyclerItemClick) :
    BottomSheetDialogFragment(), NextAdOnAdapter.Selected {
    var bi: FragmentSubAdOnSheetBinding? = null
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bottomSheet =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view =
            View.inflate(context, R.layout.fragment_sub_ad_on_sheet, null)

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
                }
            }
        })

        bi!!.tvBack.setOnClickListener { dismiss() }
        bi!!.btnAdd.setOnClickListener {
            iRecyclerItemClick.onItemClick(itemSubCategory)
            dismiss()
        }
        bi!!.rvInnerOptions.adapter = NextAdOnAdapter(itemSubCategory.item_sub_sub_category, this)
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


    override fun isSelected(selected: Boolean, position: Int) {
        itemSubCategory.item_sub_sub_category[position].checked = selected
    }


}









