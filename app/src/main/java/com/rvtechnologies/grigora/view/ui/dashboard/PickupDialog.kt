package com.rvtechnologies.grigora.view.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view_model.SendMoneyConfirmationViewModel
import kotlinx.android.synthetic.main.fragment_pickup_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class PickupDialog(var iRecyclerItemClick: IRecyclerItemClick) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pickup_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        bt_view.setOnClickListener {
            iRecyclerItemClick.onItemClick(1)
            dismiss()
        }

        bt_dismiss.setOnClickListener {
            iRecyclerItemClick.onItemClick(2)
            dismiss()
        }

    }
}









