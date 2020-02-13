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
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ProfileFragmentBinding
import com.rvtechnologies.grigora.model.ContactUsModel
import com.rvtechnologies.grigora.model.models.LogoutModel
import com.rvtechnologies.grigora.utils.ApiConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.CommonUtils.delPrefValue
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.login_signup.LoginFragment
import com.rvtechnologies.grigorahq.network.ConnectionNetwork
import com.rvtechnologies.grigorahq.network.EventBroadcaster
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_login.view.*
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : Fragment(), EventBroadcaster {
    override fun broadcast(code: Int, data: Any?) {
        if (code == 800) {
            var pojo = Gson().fromJson(data.toString(), LogoutModel::class.java)
            if (pojo.status == 1) {
                CommonUtils.savePrefs(context, PrefConstants.TOKEN, "")
                (activity as MainActivity).nav_view.setCheckedItem(R.id.navigationRestaurants)
            } else {
                ConnectionNetwork.showSnack(
                    false,
                    activity!!,
                    parent_layout_contact,
                    getString(R.string.went_wrong)
                )
            }
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
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
        GrigoraApp.getInstance()!!.registerListener(this)

        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle(getString(R.string.my_account))
            (activity as MainActivity).lockDrawer(true)
            (activity as MainActivity).img_back.visibility = View.GONE
            (activity as MainActivity).showBottomNavigation(4)
        }

        val token = CommonUtils.getPrefValue(context, PrefConstants.TOKEN)
        if (token.isBlank()) {
            showLoginAlert(activity as MainActivity?)
        }

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


    private fun showLoginAlert(activity: MainActivity?) {
        var alertDialog: AlertDialog? = null

        val dialogBuilder = activity?.let { AlertDialog.Builder(it) }
        if (activity is MainActivity && !activity.isDestroyed && alertDialog == null) {
            val inflater = activity.layoutInflater
            val dialogView = inflater.inflate(R.layout.alert_login, null)
            dialogBuilder?.setView(dialogView)
            dialogBuilder?.setCancelable(false)
            dialogView.btnLogin.setOnClickListener {
                alertDialog?.dismiss()
                toLogin()
            }
            dialogView.btnLater.setOnClickListener {
                alertDialog?.dismiss()
                activity.nav_view.setCheckedItem(R.id.navigationRestaurants)
            }

            alertDialog = dialogBuilder?.create()

            alertDialog?.show()
        }
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

    fun toAboutUs() {
        view?.findNavController()
            ?.navigate(R.id.action_navigationMyAccounts_to_aboutUsFragment)
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
        setLogoutApi()

//        CommonUtils.savePrefs(context,PrefConstants.TOKEN,"")
//        (activity as MainActivity).nav_view.selectedItemId=R.id.navigationRestaurants
    }

    private fun setLogoutApi() {
        var data = HashMap<String, Any?>()

        var headerMAp = HashMap<String, Any>()
        headerMAp.put("Authorization", CommonUtils.getPrefValue(activity!!, PrefConstants.TOKEN))
        data.put("device_id", device_id())
        ConnectionNetwork.postFormData(
            ApiConstants.LOGOUT,
            headerMAp,
            data,
            "",
            activity!!,
            sv_parent,
            800
        )
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
        GrigoraApp.getInstance()!!.deRegisterListener(this)

    }
}
