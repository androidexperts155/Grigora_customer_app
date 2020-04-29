package com.rvtechnologies.grigora.view.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.fragment_schedule_alert.*

/**
 * A simple [Fragment] subclass.
 */
class ScheduleAlert(var iRecyclerItemClick: IRecyclerItemClick) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_schedule_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.isCancelable=false
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        bt_submit.setOnClickListener {
            iRecyclerItemClick.onItemClick(true)
            dismiss()
        }
        img_close.setOnClickListener {
            iRecyclerItemClick.onItemClick(false)
            dismiss()

        }
    }
}
