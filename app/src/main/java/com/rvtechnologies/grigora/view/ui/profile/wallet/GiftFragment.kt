package com.rvtechnologies.grigora.view.ui.profile.wallet

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayout

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.VoucherCodeModel
import com.rvtechnologies.grigora.model.VoucherModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.*
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.orders.PaymentOptionsDialog
import com.rvtechnologies.grigora.view.ui.payment.PaymentActivity
import com.rvtechnologies.grigora.view_model.SharedGiftViewModel
import kotlinx.android.synthetic.main.gift_fragment.*

class GiftFragment : Fragment(), IRecyclerItemClick {
    companion object {
        fun newInstance() = GiftFragment()
    }

    private lateinit var viewModel: SharedGiftViewModel
    private var vouchers = ArrayList<VoucherModel>()
    lateinit var voucherCodeModel: VoucherCodeModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            activity!!.let { ViewModelProviders.of(it).get(SharedGiftViewModel::class.java) }
        viewModel.voucherCodes.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    vouchers.clear()
                    vouchers.addAll(response.data as ArrayList<VoucherModel>)
                    viewModel.selectedVoucher.value = vouchers[0]

                    tv_money.text = "₦ " + vouchers[0].amount.toString()
                    tv_money_info.text =
                        if (GrigoraApp.getInstance()
                                .getCurrentLanguage() == AppConstants.FRENCH
                        ) vouchers[0].created_atamount_french_name!! else vouchers[0].amount_english_name!!

                } else {
                    CommonUtils.showMessage(parent, response?.message!!)
                }
            } else if (response != null) {
                CommonUtils.showMessage(parent, response.toString())
            }
        })

        viewModel.voucherCode.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    var codeModel = response.data as VoucherCodeModel
                    var dialog =
                        ShowVoucherFragment(codeModel, this, viewModel.isSelfSelected.value!!)
                    dialog.show(childFragmentManager, "")

                } else {
                    CommonUtils.showMessage(parent, response?.message!!)
                }
            } else if (response != null) {
                CommonUtils.showMessage(parent, response.toString())
            }
        })

        viewModel.sendGift.observe(this, Observer { response ->
            if (response is CommonResponseModel<*>) {
                if (response.status!!) {
                    CommonUtils.showMessage(parent, response?.message!!)
                    (activity as MainActivity).clearStack()
                    (activity as MainActivity).selectedNavigation(R.id.dashBoardFragment)
//                    view?.findNavController()
//                        ?.navigate(R.id.action_giftFragment_to_dashboard)
                } else {
                    CommonUtils.showMessage(parent, response?.message!!)
                }
            } else if (response != null) {
                CommonUtils.showMessage(parent, response.toString())
            }
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context, getString(R.string.loading))
            } else {
                CommonUtils.hideLoader()
            }
        })

        viewModel.isSelfSelected.value = true
    }
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.gift_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rel_user.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_giftFragment_to_chooseUserFragment)
        }

        rel_price.setOnClickListener {
            var dialog = SelectAmountDialog(this, vouchers)
            dialog.show(childFragmentManager, "")
        }

        bt_add_new.setOnClickListener {

            if (viewModel.selectedUser.value != null || viewModel.isSelfSelected.value!!) {
                viewModel.getVoucherCode(
                    CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
                )
            } else
                CommonUtils.showMessage(parent, getString(R.string.no_user_selected))
        }

        rd_others.isChecked = true

        rd_sel.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rd_others) {
                card_search.visibility = View.VISIBLE
                viewModel.isSelfSelected.value = false
            } else {
                card_search.visibility = View.GONE
                viewModel.isSelfSelected.value = true
            }
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
                    p0!!.text.toString().equals(getString(R.string.myself)) -> {
                        card_search.visibility = View.GONE
                        viewModel.isSelfSelected.value = true
                    }
                    p0!!.text.toString().equals(getString(R.string.others)) -> {
                        card_search.visibility = View.VISIBLE
                        viewModel.isSelfSelected.value = false
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideAll()
        (activity as MainActivity).backTitle(getString(R.string.grigora_gift_card))

        if (viewModel?.isSelfSelected.value == false) {
            tab_top.getTabAt(1)?.select()

        }
        if (viewModel.selectedUser.value != null) {
            tv_hint.visibility = View.GONE
            tv_name.visibility = View.VISIBLE
            tv_email.visibility = View.VISIBLE
            tv_name.text = viewModel.selectedUser.value?.name
            tv_email.text = viewModel.selectedUser.value?.username
        }

        viewModel.getVoucherCodes(
            CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN)
        )
    }

    override fun onItemClick(item: Any) {
        if (item is VoucherModel) {
            tv_money.text = "₦ " + item.amount.toString()
            viewModel.selectedVoucher.value = item
            tv_money_info.text = if (GrigoraApp.getInstance()
                    .getCurrentLanguage() == AppConstants.FRENCH
            ) item.created_atamount_french_name!! else item.amount_english_name!!
        } else if (item is VoucherCodeModel) {
            voucherCodeModel = item
            showPaymentOptionsDialog()
        } else if (item is Int) {
            if (item == 3) {
//                weallet selected
                buyOrSend("3", "")
            } else if (item == 2) {
//                paystack selected
                startActivityForResult(
                    Intent(
                        activity,
                        PaymentActivity::class.java
                    ).putExtra("amount", voucherCodeModel.amount.toDouble()), 400
                )
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy(activity as MainActivity)
    }

    fun showPaymentOptionsDialog() {
        var optionsDialog = PaymentOptionsDialog(this)
        optionsDialog.show(this.childFragmentManager, "")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 400) {
            val result = data?.getStringExtra("reference")
            if (result != null) {
                buyOrSend("2", result)
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // the user canceled
        } else {
            // handle errors here, an exception may be available in

        }
    }

    fun buyOrSend(type: String, ref: String) {
        if (::voucherCodeModel.isInitialized)
            if (viewModel.isSelfSelected.value!!)
                viewModel.buyCard(
                    CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
                    voucherCodeModel, type, ref
                )
            else
                viewModel.sendGift(
                    CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN),
                    voucherCodeModel, type, ref
                )
    }
}
