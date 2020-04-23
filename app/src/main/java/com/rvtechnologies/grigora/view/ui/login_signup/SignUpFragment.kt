package com.rvtechnologies.grigora.view.ui.login_signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.FragmentSignUpBinding
import com.rvtechnologies.grigora.model.models.LoginResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.SignUpFragmentViewModel
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.parentView

class SignUpFragment : Fragment() {
    private var signUpViewModel: SignUpFragmentViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonUtils.changeStatusBarColor(
            ContextCompat.getColor(context!!, R.color.lightGrey),
            activity as MainActivity
        )
        signUpViewModel = ViewModelProviders.of(this).get(SignUpFragmentViewModel::class.java)
        signUpViewModel?.signUpResult?.observe(this, Observer { response ->
            if (response is LoginResponseModel) {
                CommonUtils.showMessage(parentView, "Welcome " + response.data?.name)
                saveData(response)
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        signUpViewModel?.isLoading?.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context!!, "Signing Up")
            } else {
                CommonUtils.hideLoader()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val signUpFragmentBinding: FragmentSignUpBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_up, container, false
        )
        signUpFragmentBinding.signUpFragmentViewModel = signUpViewModel
        signUpFragmentBinding.signUpFragment = this
        return signUpFragmentBinding.root
    }

    private fun saveData(data: LoginResponseModel) {
        CommonUtils.savePrefs(context, PrefConstants.TOKEN, data.tokenType + " " + data.accessToken)
        CommonUtils.savePrefs(context, PrefConstants.ID, data.data?.id?.toString())
        CommonUtils.savePrefs(context, PrefConstants.NAME, data.data?.name?.toString())
        CommonUtils.savePrefs(context, PrefConstants.IMAGE, data.data?.image?.toString())
        CommonUtils.savePrefs(context, PrefConstants.PIN, data.data?.pin?.toString())
        CommonUtils.savePrefs(context, PrefConstants.EMAIL, data.data?.email?.toString())

        back()
    }

    fun back() {
        activity?.onBackPressed()
    }

    fun toLogin() {
        activity?.onBackPressed()
    }

    fun signUp() {
        if (signUpViewModel?.isValidData()!!) {
            startActivityForResult(
                Intent(context, OtpActivity::class.java).putExtra(
                    "phone",
                    "+" + ccp.selectedCountryCodeWithPlus.toString() + signUpViewModel?.phone?.value
                ), AppConstants.OTP_CODE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle("")
            (activity as MainActivity).lockDrawer(true)
        }
    }

    fun termsAndConditions() {
        var bundle = bundleOf(AppConstants.PAGE_TYPE to 2)
        view?.findNavController()?.navigate(R.id.action_signUpFragment_to_aboutUs, bundle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.OTP_CODE) {
            if (data?.getBooleanExtra("verified", false)!!) {
                signUpViewModel?.signUp()
            }
        } else {
            var message = data!!.getStringExtra("message")
            CommonUtils.showMessage(parentView, message)
        }
    }
}
