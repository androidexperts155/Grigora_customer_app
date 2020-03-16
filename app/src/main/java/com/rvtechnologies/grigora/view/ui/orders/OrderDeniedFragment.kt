package com.rvtechnologies.grigora.view.ui.orders


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.fragment_order_denied.*

class OrderDeniedFragment(

    val type: Int,
    val text: String,
    val iRecyclerItemClick: IRecyclerItemClick
) :
    DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_denied, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)

        tv_title.text = text

        if (type == 3) {
            bt_ok.visibility = View.GONE
            li_continue.visibility = View.VISIBLE

        } else {
            bt_ok.visibility = View.VISIBLE
            li_continue.visibility = View.GONE
        }

        bt_ok.setOnClickListener {
            dismiss()
            iRecyclerItemClick.onItemClick("1")
        }
        bt_continue.setOnClickListener {
            dismiss()
            iRecyclerItemClick.onItemClick("2")
        }
        bt_cancel.setOnClickListener {
            dismiss()
            iRecyclerItemClick.onItemClick("1")
        }
    }


}
