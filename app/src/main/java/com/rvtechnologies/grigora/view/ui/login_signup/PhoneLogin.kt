package com.rvtechnologies.grigora.view.ui.login_signup

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth

import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.FragmentLoginBinding
import com.rvtechnologies.grigora.databinding.PhoneLoginFragmentBinding
import com.rvtechnologies.grigora.model.models.LoginResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.LoginFragmentViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.parentView
import kotlinx.android.synthetic.main.phone_login_fragment.*

class PhoneLogin : Fragment() {

    companion object {
        fun newInstance() = PhoneLogin()
    }

    private lateinit var viewModel: LoginFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(LoginFragmentViewModel::class.java)

        CommonUtils.changeStatusBarColor(
            ContextCompat.getColor(context!!, R.color.lightGrey),
            activity as MainActivity
        )

        viewModel?.loginResult?.observe(this, Observer { response ->
            if (response is LoginResponseModel) {
                CommonUtils.showMessage(parentView, "Welcome " + response.data?.name)
                saveData(response)
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        viewModel?.isLoading?.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context!!, "Verifying")
            } else {
                CommonUtils.hideLoader()
            }
        })


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val loginFragmentBinding: PhoneLoginFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.phone_login_fragment, container, false
        )
        loginFragmentBinding.loginFragmentViewModel = viewModel
        loginFragmentBinding.phoneLoginFragment = this
        return loginFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun toLogin() {
        view?.findNavController()?.navigate(R.id.action_phoneLogin_fragment_to_login)
    }

    fun toOTP() {
        if (viewModel?.isValidPhone()!!) {

            var auth = FirebaseAuth.getInstance()
            if (auth.currentUser != null)
                auth.signOut()

            startActivityForResult(
                Intent(context, OtpActivity::class.java).putExtra(
                    "phone",
                    ccp.selectedCountryCodeWithPlus + viewModel?.email?.value
                ), AppConstants.OTP_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.OTP_CODE) {
            if (data?.extras != null && data?.extras?.containsKey("verified")!!)
                if (data?.getBooleanExtra("verified", false)!!)
                    viewModel.phoneLogin()
        }
    }

    private fun saveData(data: LoginResponseModel) {
        CommonUtils.savePrefs(context, PrefConstants.TOKEN, data.tokenType + " " + data.accessToken)
        CommonUtils.savePrefs(context, PrefConstants.ID, data.data?.id?.toString())
        CommonUtils.savePrefs(context, PrefConstants.NAME, data.data?.name?.toString())
        CommonUtils.savePrefs(context, PrefConstants.IMAGE, data.data?.image?.toString())

        if (data?.data?.have_address!!) {
            view?.findNavController()
                ?.navigate(R.id.action_phoneLogin_fragment_to_addressListFragment)
        } else {
            view?.findNavController()
                ?.navigate(R.id.action_phoneLogin_fragment_to_selectLocationFragment)
        }
    }

}
