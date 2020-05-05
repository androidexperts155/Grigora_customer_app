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
import com.rvtechnologies.grigora.model.models.CommonResponseModel
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

    var emailAlreadyExists = false
    var phoneAlreadyExists = false


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
                if (response.status!!) {
                    CommonUtils.showMessage(parentView, getString(R.string.verify_email))
                    saveData(response)
                } else
                    CommonUtils.showMessage(parentView, response.message!!)
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

        signUpViewModel?.checkEmail?.observe(this, Observer { res ->
            if (res is CommonResponseModel<*>) {
                if (res.status!!) {
                    ed_email.error = getString(R.string.already_exists)
                    emailAlreadyExists = true
                } else
                    emailAlreadyExists = false
            }

        })



        signUpViewModel?.checkPhone?.observe(this, Observer { res ->
            if (res is CommonResponseModel<*>) {
                if (res.status!!) {
                    ed_phone.error = getString(R.string.already_exists)
                    phoneAlreadyExists = true
                } else
                    phoneAlreadyExists = false
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ed_email.onFocusChangeListener = View.OnFocusChangeListener { view, b -> hasEmailFocus(b) }
        ed_phone.onFocusChangeListener = View.OnFocusChangeListener { view, b -> hasPhoneFocus(b) }
        ed_name.onFocusChangeListener = View.OnFocusChangeListener { view, b -> hasNameFocus(b) }
        ed_password.onFocusChangeListener =
            View.OnFocusChangeListener { view, b -> hasPasswordFocus(b) }
        ed_confirm_password.onFocusChangeListener =
            View.OnFocusChangeListener { view, b -> hasConfirmPasswordFocus(b) }
    }

    private fun hasEmailFocus(has: Boolean) {
        if (!has) {
            if (!CommonUtils.isValidEmail(ed_email.text.toString()))
                ed_email.error = getString(R.string.invalid_email)
            else signUpViewModel?.checkEmail()
        }


    }

    private fun hasPhoneFocus(has: Boolean) {
        if (!has) {
            if (!CommonUtils.isValidPhone(ed_phone.text.toString()))
                ed_phone.error = getString(R.string.invalid_phone)
            else
                signUpViewModel?.checkPhone(ccp.selectedCountryCodeWithPlus + ed_phone.text.toString())

        }
    }

    private fun hasNameFocus(has: Boolean) {
        if (!has)
            if (ed_name.text.toString().isNullOrEmpty())
                ed_name.error = getString(R.string.invalid_name)
    }

    private fun hasPasswordFocus(has: Boolean) {
        if (!has)
            if (!CommonUtils.isValidPassword(ed_password.text.toString()))
                ed_password.error = getString(R.string.invalid_password)
    }

    private fun hasConfirmPasswordFocus(has: Boolean) {
        if (!has)
            if (ed_password.text.toString() != ed_confirm_password.text.toString())
                ed_confirm_password.error = getString(R.string.invalid_confirm_password)

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
        if (isValidData()) {
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
            if (data!=null && data.extras!!.containsKey("verified"))
                if (data.getBooleanExtra("verified", false)) {
                    signUpViewModel?.signUp(ccp.selectedCountryCodeWithPlus)
                }
        } else if(data!=null && data.hasExtra("message")) {
            var message = data.getStringExtra("message")
            CommonUtils.showMessage(parentView, message)
        }
    }

    private fun isValidData(): Boolean {
        var valid = true

        if (!CommonUtils.isValidPhone(ed_phone.text.toString())) {
            valid = false
            ed_phone.error = getString(R.string.invalid_phone)
        } else if (phoneAlreadyExists) {
            valid = false
            ed_phone.error = getString(R.string.already_exists)
        }


        if (!CommonUtils.isValidEmail(ed_email.text.toString())) {
            valid = false
            ed_email.error = getString(R.string.invalid_email)
        } else if (emailAlreadyExists) {
            valid = false
            ed_email.error = getString(R.string.already_exists)
        }

        if (ed_name.text.toString().isNullOrEmpty()) {
            valid = false
            ed_name.error = getString(R.string.invalid_name)
        }

        if (!CommonUtils.isValidPassword(ed_password.text.toString())) {
            valid = false
            ed_password.error = getString(R.string.invalid_password)
        } else if (ed_password.text.toString() != ed_confirm_password.text.toString()) {
            valid = false
            ed_confirm_password.error = getString(R.string.invalid_confirm_password)
        }

        if (!chk_terms_text.isChecked) {
            valid = false
            CommonUtils.showMessage(parent, getString(R.string.please_check_terms))
        }



        return valid
    }
}
