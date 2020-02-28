package com.rvtechnologies.grigora.view.ui.profile.wallet


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import kotlinx.android.synthetic.main.fragment_transfer_success_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class TransferSuccessDialog(val iRecyclerItemClick: IRecyclerItemClick) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transfer_success_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        bt_submit.setOnClickListener {
            dismiss()
            iRecyclerItemClick.onItemClick(1)
        }
    }


}
