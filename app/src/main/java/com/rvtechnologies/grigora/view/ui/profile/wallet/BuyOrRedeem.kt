package com.rvtechnologies.grigora.view.ui.profile.wallet

import android.Manifest
import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.ReceivedVoucherModel
import com.rvtechnologies.grigora.model.WalletHistoryModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.profile.wallet.adapter.VouchersAdapter
import com.rvtechnologies.grigora.view_model.BuyOrRedeemViewModel
import kotlinx.android.synthetic.main.buy_or_redeem_fragment.*


class BuyOrRedeem : Fragment(), IRecyclerItemClick {
    lateinit var historyModel: WalletHistoryModel

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
                    viewModel.getHistory(CommonUtils.getToken())
                    viewModel.getCards(
                        CommonUtils.getToken()
                    )
                } else {
                    CommonUtils.showMessage(parent, res.message!!)
                }
            } else {
                CommonUtils.showMessage(parent, res.toString())
            }

        })

        viewModel.historyResponse.observe(this, Observer { historyRes ->
            if (historyRes is CommonResponseModel<*>) {
                historyModel = historyRes.data as WalletHistoryModel
                wallet.text = "₦ " + CommonUtils.getRoundedOff(historyModel.wallet.toDouble())

                tv_points.text =
                    "₦ " + CommonUtils.getRoundedOff(((historyModel.wallet.toDouble()) * (historyModel.naira_to_points).toDouble()))
                tv_wallet_id.text = historyModel.wallet_id

            } else {
                CommonUtils.showMessage(parent, historyRes.toString())
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

        img_scanner.setOnClickListener {
            Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        IntentIntegrator.forSupportFragment(this@BuyOrRedeem)
                            .initiateScan()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest,
                        token: PermissionToken
                    ) {

                    }
                }).check()


        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).lockDrawer(true)
        (activity as MainActivity).backTitle("")
        viewModel.getCards(
            CommonUtils.getToken()
        )
        viewModel.getHistory(CommonUtils.getToken())
    }

    private fun redeem(item: String) {
        viewModel.redeem(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN), item)
        ed_wallet_id.setText("")
    }

    override fun onItemClick(item: Any) {
        if (item is ReceivedVoucherModel) {
            ed_wallet_id.setText(item.voucher_code)
//            redeem(item.voucher_code)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {

            } else {
                ed_wallet_id.setText(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
