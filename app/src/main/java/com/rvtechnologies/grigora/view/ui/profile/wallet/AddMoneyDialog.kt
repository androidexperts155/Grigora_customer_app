package com.rvtechnologies.grigora.view.ui.profile.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.fragment_add_money_dialog.*

class AddMoneyDialog(val iRecyclerItemClick: IRecyclerItemClick) : BottomSheetDialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_add_money_dialog, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)

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

    fun pay() {
        if (!ed_money.text.toString().isNullOrEmpty()) {
            iRecyclerItemClick.onItemClick(ed_money.text.toString())
            this.dismiss()
        } else
            CommonUtils.showMessage(view, getString(R.string.add_amount))
    }


    fun op1() {
        ed_money.setText("100")
        ed_money.setSelection(ed_money.text.toString().length)
    }

    fun op2() {
        ed_money.setText("200")
        ed_money.setSelection(ed_money.text.toString().length)

    }

    fun op3() {
        ed_money.setText("500")
        ed_money.setSelection(ed_money.text.toString().length)

    }

}
