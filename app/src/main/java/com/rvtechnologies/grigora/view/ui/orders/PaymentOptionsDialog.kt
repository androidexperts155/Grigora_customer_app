package com.rvtechnologies.grigora.view.ui.orders


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import kotlinx.android.synthetic.main.dialog_payment_options.*

/**
 * A simple [Fragment] subclass.
 */
class PaymentOptionsDialog(
    val iRecyclerItemClick: IRecyclerItemClick
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_payment_options, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_wallet.text = getString(
            R.string.wallet, CommonUtils.getPrefValue(
                context!!,
                PrefConstants.WALLET
            )
        )

        img_cancel.setOnClickListener {
            iRecyclerItemClick.onItemClick(0)
            this.dismiss()
        }

        li_pod.setOnClickListener {
            iRecyclerItemClick.onItemClick(1)
            this.dismiss()
        }

        li_paystack.setOnClickListener {
            iRecyclerItemClick.onItemClick(2)
            this.dismiss()
        }

        li_wallet.setOnClickListener {
            iRecyclerItemClick.onItemClick(3)
            this.dismiss()
        }
    }


}
