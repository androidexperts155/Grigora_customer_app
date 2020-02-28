package com.rvtechnologies.grigora.view.ui.profile.wallet

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayout

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.TransferMoneyFragmentBinding
import com.rvtechnologies.grigora.model.WalletHistoryModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.profile.wallet.adapter.HistoryAdapter
import com.rvtechnologies.grigora.view_model.TransferMoneyViewModel
import kotlinx.android.synthetic.main.transfer_money_fragment.*
import kotlinx.android.synthetic.main.transfer_money_fragment.parentView
import kotlinx.android.synthetic.main.transfer_money_fragment.wallet

class TransferMoney : Fragment(), IRecyclerItemClick {
    private lateinit var viewModel: TransferMoneyViewModel
    lateinit var historyModel: WalletHistoryModel
    var amount = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TransferMoneyViewModel::class.java)

        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

        viewModel.historyResponse.observe(this, Observer { historyRes ->
            if (historyRes is CommonResponseModel<*>) {
                historyModel = historyRes.data as WalletHistoryModel
                wallet.text = "₦ " + historyModel.wallet
                tv_points.text =
                    "₦ " + ((historyModel.wallet.toDouble()) * (historyModel.naira_to_points).toDouble()).toString()
                tv_wallet_id.text = historyModel.wallet_id


                handleHistory()

            } else {
                CommonUtils.showMessage(parentView, historyRes.toString())
            }
        })

        viewModel.transferResponse.observe(this, Observer { transferResponse ->
            if (transferResponse is CommonResponseModel<*>) {
                if (transferResponse.status!!) {
                    var transferSuccessDialog = TransferSuccessDialog(this)
                    transferSuccessDialog.show(childFragmentManager, "")
                } else
                    CommonUtils.showMessage(parentView, transferResponse.message!!)


            } else {
                CommonUtils.showMessage(parentView, transferResponse.toString())
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
            R.layout.transfer_money_fragment,
            container,
            false
        ) as TransferMoneyFragmentBinding
        walletFragmentBinding.walletViewModel = viewModel
        walletFragmentBinding.walletView = this
        return walletFragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.getBoolean(AppConstants.IS_FOR_HISTORY)!!) {
            li_transfer.visibility = View.GONE
        }

        for (i in 0 until tab_top.tabCount) {

            //noinspection ConstantConditions
            var tv = layoutInflater.inflate(R.layout.tab_textview, null) as TextView
            tv.gravity = Gravity.CENTER
            tv.text = tab_top.getTabAt(i)?.text

            if (tab_top.getTabAt(i)?.isSelected!!) {
                tv.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
            } else {
                if (CommonUtils.isDarkMode())
                    tv.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                else
                    tv.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))
            }
            tab_top.getTabAt(i)?.customView = tv

        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getHistory()

        if (activity is MainActivity) {

            (activity as MainActivity).hideAll()
            (activity as MainActivity).lockDrawer(true)
        }
    }

    private fun handleHistory() {


        if (historyModel.history.isNullOrEmpty()) {
            li_transactions.visibility = View.GONE
            not_found.visibility = View.VISIBLE
        } else {
            li_transactions.visibility = View.VISIBLE
            not_found.visibility = View.GONE

            rc_history.adapter = HistoryAdapter(historyModel.history)
        }

//        3=>add in grigora wallet,4=>deduct from grigora wallet,5:send money,6:receive money

        tab_top.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                var view: View? = p0!!.customView

                view as TextView

                if (CommonUtils.isDarkMode())
                    view.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                else
                    view.setTextColor(ContextCompat.getColor(context!!, R.color.textBlack))


            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                var view: View? = p0!!.customView
                view as TextView
                view.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))

                when {
                    p0!!.text.toString().equals(getString(R.string.all)) -> {
                        rc_history.adapter = HistoryAdapter(historyModel.history)
                    }
                    p0!!.text.toString().equals(getString(R.string.transfered)) -> {
                        var tempHistory = ArrayList<WalletHistoryModel.History>()
                        for (item in historyModel.history) {
                            if (item.type == "5") {
                                tempHistory.add(item)
                            }
                        }

                        rc_history.adapter = HistoryAdapter(tempHistory)

                    }
                    p0!!.text.toString().equals(getString(R.string.received)) -> {
                        var tempHistory = ArrayList<WalletHistoryModel.History>()
                        for (item in historyModel.history) {
                            if (item.type == "6") {
                                tempHistory.add(item)
                            }
                        }

                        rc_history.adapter = HistoryAdapter(tempHistory)
                    }
                }
            }
        })


    }

    fun back() {
        view?.findNavController()?.popBackStack()
    }

    fun transfer() {
        when {
            ed_wallet_id.text.trim().toString().length < 8 -> {
                ed_wallet_id.error = getString(R.string.invalid_wallet_id)
            }
            ed_amount.text.trim().toString().toDouble() <= 0 -> {
                ed_amount.error = getString(R.string.please_enter_amount)
            }
            else -> {
                viewModel.transfer(
                    ed_amount.text.trim().toString(),
                    ed_wallet_id.text.trim().toString()
                )
            }
        }
    }

    override fun onItemClick(item: Any) {
        view?.findNavController()!!.popBackStack()
    }


}

