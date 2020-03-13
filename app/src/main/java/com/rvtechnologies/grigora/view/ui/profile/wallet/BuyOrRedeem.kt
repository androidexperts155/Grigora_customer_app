package com.rvtechnologies.grigora.view.ui.profile.wallet

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.ReceivedVoucherModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.profile.wallet.adapter.VouchersAdapter
import com.rvtechnologies.grigora.view_model.BuyOrRedeemViewModel
import kotlinx.android.synthetic.main.buy_or_redeem_fragment.*

class BuyOrRedeem : Fragment(), IRecyclerItemClick {

    companion object {
        fun newInstance() = BuyOrRedeem()
    }

    private var receivedVouchers = ArrayList<ReceivedVoucherModel>()

    private lateinit var viewModel: BuyOrRedeemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BuyOrRedeemViewModel::class.java)
        viewModel.cards.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    receivedVouchers.clear()
                    receivedVouchers.addAll(response.data as ArrayList<ReceivedVoucherModel>)
                    rc_vouchers.adapter = VouchersAdapter(receivedVouchers, this)

                } else {
                    CommonUtils.showMessage(parent, response?.message!!)
                }
            } else {
                CommonUtils.showMessage(parent, response.toString())
            }
        })
        viewModel.redeemRes.observe(this, Observer { res ->
            if (res is CommonResponseModel<*>) {
                if (res.status!!) {
                    CommonUtils.showMessage(parent, res.message!!)
                    view?.findNavController()?.popBackStack()
                } else {
                    CommonUtils.showMessage(parent, res.message!!)
                }
            } else {
                CommonUtils.showMessage(parent, res.toString())
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
        return inflater.inflate(R.layout.buy_or_redeem_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bt_buy.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_buyOrRedeem_to_giftFragment)
        }
        bt_add_new.setOnClickListener {
            if (!ed_wallet_id.text.toString().isNullOrEmpty())
                redeem(ed_wallet_id.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).lockDrawer(true)
        (activity as MainActivity).backTitle("")
        viewModel.getCards(
            CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
        )
    }

    private fun redeem(item: String) {
        viewModel.redeem(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN), item)
    }

    override fun onItemClick(item: Any) {
        if (item is ReceivedVoucherModel) {
            redeem(item.voucher_code)
        }
    }


}
