package com.rvtechnologies.grigora.view.ui.profile.wallet

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.WalletFragmentBinding
import com.rvtechnologies.grigora.model.AddMoneyModel
import com.rvtechnologies.grigora.model.WalletHistoryModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.PaymentActivity
import com.rvtechnologies.grigora.view_model.WalletViewModel
import kotlinx.android.synthetic.main.wallet_fragment.*

class WalletFragment : Fragment(), IRecyclerItemClick {
    companion object {
        fun newInstance() =
            WalletFragment()
    }

    lateinit var historyModel: WalletHistoryModel
    var amount = ""

    private lateinit var viewModel: WalletViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WalletViewModel::class.java)

        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

        viewModel.historyResponse.observe(this, Observer { historyRes ->
            if (historyRes is CommonResponseModel<*>) {
                historyModel = historyRes.data as WalletHistoryModel
                wallet.text = "₦ " + historyModel.wallet
                tv_points.text =
                    "₦ " + ((historyModel.wallet.toDouble()) * (historyModel.naira_to_points).toDouble()).toString()
                tv_to_naira.text =
                    getString(R.string.ngn_to_how_naira, historyModel.naira_to_points)
                tv_wallet_id.text = historyModel.wallet_id

            } else {
                CommonUtils.showMessage(parentView, historyRes.toString())
            }
        })

        viewModel.addMoneyResponse.observe(this, Observer { addMoneyRes ->
            if (addMoneyRes is AddMoneyModel) {
                wallet.text = "₦ " + addMoneyRes.wallet.toString()
            } else {
                CommonUtils.showMessage(parentView, addMoneyRes.toString())
            }
        })



        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context, getString(R.string.loading))
            } else {
                CommonUtils.hideLoader()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val walletFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.wallet_fragment,
            container,
            false
        ) as WalletFragmentBinding
        walletFragmentBinding.walletViewModel = viewModel
        walletFragmentBinding.walletView = this
        return walletFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back.setOnClickListener { view?.findNavController().popBackStack() }
        ed_naira.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s?.trim().toString().isNullOrEmpty()) {
                    tv_pts.text =
                        (s?.trim().toString().toDouble() * (historyModel.naira_to_points).toDouble()).toString()
                } else
                    tv_pts.text = "0"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getHistory()

        if (activity is MainActivity) {

            (activity as MainActivity).hideAll()
            (activity as MainActivity).lockDrawer(true)
        }
    }

    fun transferMoney() {
        var bundle = bundleOf(AppConstants.IS_FOR_HISTORY to false)
        view?.findNavController()
            ?.navigate(R.id.action_walletFragment_to_sendMoneyFragment, bundle)
    }

    fun backPress() {
        view?.findNavController()?.popBackStack()
    }

    fun add() {
        var addMoneyDialog = AddMoneyDialog(this,historyModel.naira_to_points)
        addMoneyDialog.show(this.childFragmentManager, "")
    }

    fun seeAllTransactions(){
        var bundle = bundleOf(AppConstants.IS_FOR_HISTORY to true)
        view?.findNavController()
            ?.navigate(R.id.action_walletFragment_to_sendMoneyFragment, bundle)
    }
    fun buyOrRedeem(){
        view?.findNavController()
            ?.navigate(R.id.action_walletFragment_to_buyOrRedeem)
    }

    override fun onItemClick(item: Any) {

        amount = item.toString()
        startActivityForResult(
            Intent(
                activity,
                PaymentActivity::class.java
            ), 400
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 400) {
            val result = data?.getStringExtra("reference")
            if (result != null) {
                viewModel.addMoney(result, amount)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // the user canceled
        } else {
            // handle errors here, an exception may be available in

        }
    }
}
