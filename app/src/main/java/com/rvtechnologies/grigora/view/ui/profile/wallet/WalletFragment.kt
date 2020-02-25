package com.rvtechnologies.grigora.view.ui.profile.wallet

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.WalletFragmentBinding
import com.rvtechnologies.grigora.model.AddMoneyModel
import com.rvtechnologies.grigora.model.WalletHistoryModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.PaymentActivity
import com.rvtechnologies.grigora.view.ui.profile.wallet.adapter.HistoryAdapter
import com.rvtechnologies.grigora.view_model.WalletViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.wallet_fragment.*

class WalletFragment : Fragment(), IRecyclerItemClick {
    companion object {
        fun newInstance() =
            WalletFragment()
    }

    var amount = ""
    var historyList = ArrayList<WalletHistoryModel.Data>()

    private lateinit var viewModel: WalletViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WalletViewModel::class.java)

        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

        viewModel.historyResponse.observe(this, Observer { historyRes ->
            if (historyRes is WalletHistoryModel) {
                historyList.clear()
                wallet.text = "₦ " + historyRes.wallet
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

    }

    override fun onResume() {
        super.onResume()
        viewModel.getHistory()

        if (activity is MainActivity) {

            (activity as MainActivity).hideAll()
            (activity as MainActivity).lockDrawer(true)
        }
    }

    fun sendMoney() {
        view?.findNavController()
            ?.navigate(R.id.action_walletFragment_to_sendMoneyFragment)
    }

    fun backPress() {
        view?.findNavController()?.popBackStack()
    }

    fun add() {
        var addMoneyDialog = AddMoneyDialog(this)
        addMoneyDialog.show(this.childFragmentManager, "")
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
