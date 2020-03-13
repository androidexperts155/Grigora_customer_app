package com.rvtechnologies.grigora.view.ui.profile

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ProfileFragmentBinding
import com.rvtechnologies.grigora.model.WalletHistoryModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.LogoutModel
import com.rvtechnologies.grigora.utils.ApiConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.ProfileViewModel
import com.rvtechnologies.grigora.view_model.SettingViewModel
import com.rvtechnologies.grigorahq.network.ConnectionNetwork
import com.rvtechnologies.grigorahq.network.EventBroadcaster
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_login.view.*
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel

    lateinit var historyModel: WalletHistoryModel
    var amount = ""


    companion object {
        fun newInstance() = ProfileFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context, getString(R.string.loading))
            } else {
                CommonUtils.hideLoader()
            }
        })


        viewModel.logoutRes.observe(this, Observer {
            CommonUtils.delPrefValue(context!!)
            view?.findNavController()?.navigate(R.id.action_navigationMyAccounts_to_socialLogin)
        })

        viewModel.historyResponse.observe(this, Observer { historyRes ->
            if (historyRes is CommonResponseModel<*>) {
                historyModel = historyRes.data as WalletHistoryModel
                tv_wallet.text = "₦ ${historyModel.wallet}"
            } else {
                CommonUtils.showMessage(parentView, historyRes.toString())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val profileFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.profile_fragment,
            container,
            false
        ) as ProfileFragmentBinding
        profileFragmentBinding.profileFragment = this
        return profileFragmentBinding.root
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle(getString(R.string.my_account))
            (activity as MainActivity).lockDrawer(true)
            (activity as MainActivity).img_back.visibility = View.GONE
            (activity as MainActivity).showBottomNavigation(4)
        }
        viewModel.getHistory(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN))

    }

    fun getProfileImage(): String {
        return CommonUtils.getPrefValue(context, PrefConstants.IMAGE)
    }

    fun getName(): String {
        return CommonUtils.getPrefValue(context, PrefConstants.NAME)
    }

    fun getAddress(): String {
        return CommonUtils.getPrefValue(context, PrefConstants.ADDRESS)
    }

    fun getWallet(): String {
        return "₦ ${CommonUtils.getPrefValue(
            context!!,
            PrefConstants.WALLET
        )}"
    }


    private fun toLogin() {
        view?.findNavController()
            ?.navigate(
                R.id.action_navigationMyAccounts_to_loginFragment2
            )
    }

    fun toOrders() {
        view?.findNavController()?.navigate(R.id.action_navigationMyAccounts_to_ordersFragment)
    }

    fun toRefer() {
        view?.findNavController()?.navigate(R.id.action_navigationMyAccounts_to_refer_and_earn)
    }

    fun toSettings() {
        view?.findNavController()?.navigate(R.id.action_navigationMyAccounts_to_settingFragment)
    }

    fun toProfileDetails() {
        view?.findNavController()
            ?.navigate(R.id.action_navigationMyAccounts_to_profileDetailsFragment)
    }

    fun toTableBooking() {
        view?.findNavController()
            ?.navigate(R.id.action_navigationMyAccounts_to_tableBookingHistory)
    }

    fun toGift() {
        view?.findNavController()
            ?.navigate(R.id.action_navigationMyAccounts_to_giftFragment)
    }


    fun toAboutUs() {
        view?.findNavController()
            ?.navigate(R.id.action_navigationMyAccounts_to_aboutUsFragment)
    }

    fun toGroupOrders() {
        view?.findNavController()
            ?.navigate(R.id.action_navigationMyAccounts_to_groupOrdersFragment)
    }

    fun toContactUs() {
        view?.findNavController()
            ?.navigate(R.id.action_navigationMyAccounts_to_contactFragment)
    }

    fun toWallet() {
        view?.findNavController()
            ?.navigate(R.id.action_navigationMyAccounts_to_walletFragment)
    }

    fun logout() {
        viewModel.logout(CommonUtils.getPrefValue(context!!, PrefConstants.TOKEN), device_id())
    }

    fun device_id(): String {

        Log.e(
            "device_id", Settings.Secure.getString(
                activity!!.getContentResolver(),
                Settings.Secure.ANDROID_ID
            )
        )
        return Settings.Secure.getString(
            activity!!.getContentResolver(),
            Settings.Secure.ANDROID_ID
        );

    }

    override fun onPause() {
        super.onPause()

    }
}
