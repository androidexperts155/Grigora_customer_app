package com.rvtechnologies.grigora.view.ui.profile.wallet

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
import com.rvtechnologies.grigora.databinding.SendMoneyFragmentBinding
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.IRecyclerItemClick
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.SendMoneyViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.send_money_fragment.*

class SendMoneyFragment : Fragment(), IRecyclerItemClick {
    private lateinit var viewModel: SendMoneyViewModel
    var email = ""
    var money = ""
    var reason = ""

    companion object {
        fun newInstance() = SendMoneyFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SendMoneyViewModel::class.java)
        viewModel.token.value = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)

        viewModel.transferResponse.observe(this, Observer { transferResponse ->
            if (transferResponse is CommonResponseModel<*>) {
                CommonUtils.showMessage(parentView, transferResponse.message!!)
                view?.findNavController()?.popBackStack()
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
        val sendMoneyFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.send_money_fragment,
            container,
            false
        ) as SendMoneyFragmentBinding
        sendMoneyFragmentBinding.walletViewModel = viewModel
        sendMoneyFragmentBinding.walletView = this
        return sendMoneyFragmentBinding.root
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.VISIBLE
            (activity as MainActivity).lockDrawer(true)
        }
    }

    override fun onItemClick(item: Any) {
        if (item is Int) {
            viewModel.transfer(amount = money, email = email, reason = reason)
        } else {

        }
    }

    fun backPress() {
        view?.findNavController()?.popBackStack()
    }
}
