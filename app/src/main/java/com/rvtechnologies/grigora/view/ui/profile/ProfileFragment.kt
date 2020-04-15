package com.rvtechnologies.grigora.view.ui.profile

import android.app.Activity.RESULT_OK
import android.app.KeyguardManager
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ProfileFragmentBinding
import com.rvtechnologies.grigora.model.WalletHistoryModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.ProfileViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.profile_fragment.*


class ProfileFragment : Fragment() {
    var d=0
    private lateinit var viewModel: ProfileViewModel

    lateinit var historyModel: WalletHistoryModel
    var amount = ""

    private val LOCK_REQUEST_CODE = 221
    private val SECURITY_SETTING_REQUEST_CODE = 233
    private var keyguardManager: KeyguardManager? = null


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

            (activity as MainActivity).clearStack()
            (activity as MainActivity).setDestination(R.id.socialLoginFragment)

        })

        viewModel.historyResponse.observe(this, Observer { historyRes ->
            if (historyRes is CommonResponseModel<*>) {
                historyModel = historyRes.data as WalletHistoryModel
                tv_wallet.text = "₦ ${CommonUtils.getRoundedOff(historyModel.wallet.toDouble())}"
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!CommonUtils.isLogin()) {
            (activity as MainActivity).showLoginAlert(pop = true, id = R.id.dashBoardFragment)
        }
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

    fun toPurchasedCards() {
        view?.findNavController()?.navigate(R.id.action_navigationMyAccounts_to_purchasedCards)
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
        d=2
        authenticateApp()

    }

    fun toAboutUs() {
        var bundle = bundleOf(AppConstants.PAGE_TYPE to 1)
        view?.findNavController()
            ?.navigate(R.id.action_navigationMyAccounts_to_aboutUsFragment, bundle)
    }

    fun toTermsAndConditions() {
        var bundle = bundleOf(AppConstants.PAGE_TYPE to 2)
        view?.findNavController()
            ?.navigate(R.id.action_navigationMyAccounts_to_aboutUsFragment, bundle)
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
d=1
        authenticateApp()
    }

    fun toAddress() {
        view?.findNavController()
            ?.navigate(R.id.action_navigationMyAccounts_to_addressList)
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

    private fun authenticateApp() {

        keyguardManager =
            activity!!.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            val i = keyguardManager!!.createConfirmDeviceCredentialIntent(
                "Please unlock",
                "Confirm pin"
            );
            try {
                startActivityForResult(i, LOCK_REQUEST_CODE);
            } catch (e: Exception) {
                CommonUtils.showMessage(parentView, getString(R.string.set_lock))
                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS);
                try {
                    startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
                } catch (ex: Exception) {
                }
            }
        }
    }

    fun isDeviceSecure(): Boolean {
        activity!!.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
        //this method only work whose api level is greater than or equal to Jelly_Bean (16)
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && keyguardManager!!.isKeyguardSecure();

        //You can also use keyguardManager.isDeviceSecure(); but it requires API Level 23

    }

    //On Click of button do authentication again
    fun authenticateAgain(view: View) {
        authenticateApp();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOCK_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    if(d==1)
                    view?.findNavController()
                        ?.navigate(R.id.action_navigationMyAccounts_to_walletFragment)
                    else if(d==2)
                        view?.findNavController()
                            ?.navigate(R.id.action_navigationMyAccounts_to_giftFragment)
                } else {
                    //If screen lock authentication is failed update text
//                    textView.setText(getResources().getString(R.string.unlock_failed));
                }
            }
            SECURITY_SETTING_REQUEST_CODE -> {
                if (isDeviceSecure()) {
                    CommonUtils.showMessage(parentView, getString(R.string.correct_pin))
                    authenticateApp();
                } else {
                    //If screen lock is not enabled just update text
//                    textView.setText(getResources().getString(R.string.security_device_cancelled));
                }
            }
        }
    }

}
