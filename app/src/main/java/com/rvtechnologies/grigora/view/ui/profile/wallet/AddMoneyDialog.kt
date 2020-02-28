package com.rvtechnologies.grigora.view.ui.profile.wallet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.fragment_add_money_dialog.*

class AddMoneyDialog(val iRecyclerItemClick: IRecyclerItemClick, var naira_to_points: String) :
    BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_money_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)


        ed_naira.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s?.trim().toString().isNullOrEmpty()) {
                    tv_points.text =
                        (s?.trim().toString().toDouble() * (naira_to_points).toDouble()).toString()
                } else
                    tv_points.text = "0.00"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        bt_cancel.setOnClickListener {
            cancel()
        }

        bt_op1.setOnClickListener {
            op1()
        }

        bt_op2.setOnClickListener {
            op2()
        }

        bt_op3.setOnClickListener {
            op3()
        }

        bt_pay.setOnClickListener {
            pay()
        }


    }

    fun cancel() {
        this.dismiss()
    }

    private fun pay() {
        if (!ed_naira.text.trim().isNullOrEmpty() &&ed_naira.text.trim().toString().toDouble() > 0) {
            iRecyclerItemClick.onItemClick(ed_naira.text.toString())
            this.dismiss()
        } else
            ed_naira.error=getString(R.string.please_enter_amount)
    }


    fun op1() {
        ed_naira.setText("100.00")
        ed_naira.setSelection(ed_naira.text.toString().length)
    }

    fun op2() {
        ed_naira.setText("200.00")
        ed_naira.setSelection(ed_naira.text.toString().length)

    }

    fun op3() {
        ed_naira.setText("500.00")
        ed_naira.setSelection(ed_naira.text.toString().length)

    }

}
