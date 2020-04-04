package com.rvtechnologies.grigora.view.ui.restaurant_detail

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.FragmentNextAdonSheetBinding
import com.rvtechnologies.grigora.model.RestaurantDetailModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity


class NextAdonSheet : BottomSheetDialogFragment(), IRecyclerItemClick {
    var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    var bi: FragmentNextAdonSheetBinding? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view =
            View.inflate(context, R.layout.fragment_next_adon_sheet, null)

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

    }


}
