package com.rvtechnologies.grigora.view.ui.restaurant_list


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.dashboard.adapter.PriceFilterAdapter
import com.rvtechnologies.grigora.view.ui.restaurant_list.adapter.TimeAdapter
import kotlinx.android.synthetic.main.fragment_price_dialog.rc_items
import kotlinx.android.synthetic.main.fragment_time_bottom_sheet_dialog.*

class TimeBottomSheetDialog(
    val iRecyclerItemClick: IRecyclerItemClick,
    var l1: Int,
    var l2: Int,
    var isHour: Boolean
) :
    BottomSheetDialogFragment(), IRecyclerItemClick {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time_bottom_sheet_dialog, container, false)
    }


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (isHour)
            tv_title.text = getString(R.string.sethour)
        else
            tv_title.text = getString(R.string.setminute)

        var list = ArrayList<String>()

        for (i in l1..l2) {
            list.add(i.toString())
        }


        rc_items.adapter = TimeAdapter(list, this)
        img_close.setOnClickListener {
            dismiss()
        }
    }


    override fun onItemClick(item: Any) {
        iRecyclerItemClick.onItemClick(item)
        dismiss()
    }


}
