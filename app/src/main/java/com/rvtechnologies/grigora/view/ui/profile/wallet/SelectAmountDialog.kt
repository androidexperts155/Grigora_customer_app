package com.rvtechnologies.grigora.view.ui.profile.wallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.VoucherModel
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view.ui.profile.wallet.adapter.PriceAdapter
import kotlinx.android.synthetic.main.fragment_select_amount_dialog.*


class SelectAmountDialog(
    val iRecyclerItemClick: IRecyclerItemClick,
    var vouchers: ArrayList<VoucherModel>
) : BottomSheetDialogFragment(), IRecyclerItemClick {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_amount_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        rc_items.adapter = PriceAdapter(vouchers, this)

        bt_cancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onItemClick(item: Any) {
        iRecyclerItemClick.onItemClick(item)
        dismiss()
    }

}
