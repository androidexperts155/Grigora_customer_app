package com.rvtechnologies.grigora.view.ui.profile.wallet

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.SendMoneyConfirmationFragmentBinding
import com.rvtechnologies.grigora.model.TransferMoneyModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.view_model.SendMoneyConfirmationViewModel
import kotlinx.android.synthetic.main.send_money_confirmation_fragment.*

class SendMoneyConfirmation(
    val name: String,
    val email: String,
    val amount: String,
    val reason: String,
    val iRecyclerItemClick: IRecyclerItemClick
) : BottomSheetDialogFragment() {


    private lateinit var viewModel: SendMoneyConfirmationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.isCancelable = false

        viewModel = ViewModelProviders.of(this).get(SendMoneyConfirmationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sendMoneyConfirmationBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.send_money_confirmation_fragment,
            container,
            false
        ) as SendMoneyConfirmationFragmentBinding
        sendMoneyConfirmationBinding.confirmationViewModel = viewModel
        sendMoneyConfirmationBinding.confirmationView = this
        return sendMoneyConfirmationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (reason.isEmpty())
            tv_message.text = getString(R.string.confirmation_message1, amount, name)
        else
            tv_message.text = getString(R.string.confirmation_message, amount, name, reason)
    }

    fun transfer() {
        iRecyclerItemClick.onItemClick(1)
        this.dismiss()
     }

    fun cancel() {
        this.dismiss()
    }

}
