package com.rvtechnologies.grigora.view.ui.login_signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.FragmentLoginBinding
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.LoginResponseModel
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.LoginFragmentViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*


class LoginFragment : Fragment(), GoogleSignin {

    private var loginViewModel: LoginFragmentViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonUtils.changeStatusBarColor(
            ContextCompat.getColor(context!!, R.color.lightGrey),
            activity as MainActivity
        )
        (activity as MainActivity).initGoogleSignin(this)

        loginViewModel = ViewModelProviders.of(this).get(LoginFragmentViewModel::class.java)

        loginViewModel?.loginResult?.observe(this, Observer { response ->
            if (response is LoginResponseModel) {
                if (response.email_verified!! && response.phone_verified!!)
                    saveData(response)
                else if (!response.phone_verified!!) {
//                    navigate to otp
                    var bundle =
                        bundleOf(AppConstants.USER_ID to response.user_id, AppConstants.FROM to 2, AppConstants.PHONE to response.phone)
                    view?.findNavController()
                        ?.navigate(R.id.action_loginFragment2_to_otpFragment, bundle)
                } else if (!response.email_verified!!) {
                    CommonUtils.showMessage(parentView, getString(R.string.verify_email))
                }
            } else {
                CommonUtils.showMessage(parentView, response.toString())
            }
        })

        loginViewModel?.isLoading?.observe(this, Observer { isLoading ->
            if (isLoading) {
                CommonUtils.showLoader(context!!, "Verifying")
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

        val loginFragmentBinding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        loginFragmentBinding.loginFragmentViewModel = loginViewModel
        loginFragmentBinding.loginFragment = this
        return loginFragmentBinding.root
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).backTitle("")
            (activity as MainActivity).lockDrawer(true)
        }
    }

    private fun saveData(data: LoginResponseModel) {
        CommonUtils.savePrefs(context, PrefConstants.TOKEN, data.tokenType + " " + data.accessToken)
        CommonUtils.savePrefs(context, PrefConstants.ID, data.data?.id?.toString())
        CommonUtils.savePrefs(context, PrefConstants.NAME, data.data?.name?.toString())
        CommonUtils.savePrefs(context, PrefConstants.IMAGE, data.data?.image?.toString())
        CommonUtils.savePrefs(context, PrefConstants.PIN, data.data?.pin?.toString())
        CommonUtils.savePrefs(context, PrefConstants.EMAIL, data.data?.email?.toString())


        if (data?.data?.have_address!!) {
            view?.findNavController()?.navigate(R.id.action_loginFragment2_to_addressListFragment)
        } else {
            view?.findNavController()
                ?.navigate(R.id.action_loginFragment2_to_selectLocationFragment)
        }
    }

    fun toSignUp() {
        view?.findNavController()?.navigate(R.id.action_loginFragment2_to_signUpFragment)
    }

    fun toForgotPassword() {
        view?.findNavController()?.navigate(R.id.action_loginFragment2_to_forgotPasswordFragment)
    }

    fun back() {
        activity?.onBackPressed()
    }


    override fun signInResult(task: Task<GoogleSignInAccount>) {
        try {
            var account = task.getResult(ApiException::class.java)

        } catch (e: ApiException) {
            Log.w("Google Exception", "signInResult:failed code=" + e.statusCode)
        }
    }


    fun checkAddresses() {

    }
}

