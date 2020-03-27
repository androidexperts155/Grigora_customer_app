package com.rvtechnologies.grigora.view.ui.orders.order_denied

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_orer_denied_rider_not_available.*

/**
 * A simple [Fragment] subclass.
 */
class OrderDeniedRiderNotAvailable(val iRecyclerItemClick: IRecyclerItemClick) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_orer_denied_rider_not_available, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
        bt_continue.setOnClickListener {
            dismiss()
            iRecyclerItemClick.onItemClick("2")
        }

        bt_wait.setOnClickListener {
            dismiss()
            iRecyclerItemClick.onItemClick("3")
        }
        bt_cancel.setOnClickListener {
            dismiss()
            iRecyclerItemClick.onItemClick("1")
        }
    }

    override fun onResume() {
        super.onResume()
        this.isCancelable = false

        var displayMetrics = DisplayMetrics()
        (activity as MainActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = (displayMetrics.widthPixels - (displayMetrics.widthPixels / 9))


        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = width
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }
}


