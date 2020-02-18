package com.rvtechnologies.grigora.view.ui.restaurant_list


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.fragment_other_amount.*

class OtherAmount(val iRecyclerItemClick: IRecyclerItemClick) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_amount, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        img_close.setOnClickListener {
            dismiss()

            iRecyclerItemClick.onItemClick(0)
        }

        bt_submit.setOnClickListener {
            if (ed_count.text.toString().isNullOrEmpty()) {
                CommonUtils.showMessage(parent, getString(R.string.please_input_number))
            } else {
                dismiss()
                iRecyclerItemClick.onItemClick(ed_count.text.toString())

            }
        }

    }


}
