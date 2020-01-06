package com.rvtechnologies.grigora.view.ui.login_signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.rvtechnologies.grigora.model.models.LoginResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.LoginFragmentViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*


class LoginFragment : Fragment(), GoogleSignin {
    var gso: GoogleSignInOptions? = null
    var intent: Intent? = null
    var callbackManager: CallbackManager? = null
    var fbLoginManager: LoginManager? = null

    private var loginViewModel: LoginFragmentViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).initGoogleSignin(this)
        gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().requestId()
                .build()

        intent = GoogleSignIn.getClient(activity!!, gso!!).signInIntent



        loginViewModel = ViewModelProviders.of(this).get(LoginFragmentViewModel::class.java)

        loginViewModel?.loginResult?.observe(this, Observer { response ->
            if (response is LoginResponseModel) {
                CommonUtils.showMessage(parentView, "Welcome " + response.data?.name)
                saveData(response)
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
        callbackManager = CallbackManager.Factory.create()

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
            (activity as MainActivity).deliverLayout.visibility = View.GONE
            (activity as MainActivity).img_menu.visibility = View.GONE
            (activity as MainActivity).img_back.visibility = View.GONE
            (activity as MainActivity).lockDrawer(true)
        }
    }

    private fun saveData(data: LoginResponseModel) {
        CommonUtils.savePrefs(context, PrefConstants.TOKEN, data.tokenType + " " + data.accessToken)
        CommonUtils.savePrefs(context, PrefConstants.ID, data.data?.id?.toString())
        CommonUtils.savePrefs(context, PrefConstants.NAME, data.data?.name?.toString())
        CommonUtils.savePrefs(context, PrefConstants.IMAGE, data.data?.image?.toString())
        back()
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

    fun fbLogin() {
        fbLoginManager = LoginManager.getInstance()
        fbLoginManager?.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))

        fbLoginManager?.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.d("Facebook Token: ", result?.accessToken?.token)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
                Log.d("Facebook Error : ", error?.message)
            }
        })
    }

    fun googleLogin() {
        startActivityForResult(intent, 121)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun signInResult(task: Task<GoogleSignInAccount>) {
        try {
            var account = task.getResult(ApiException::class.java)

        } catch (e: ApiException) {
            Log.w("Google Exception", "signInResult:failed code=" + e.statusCode)
        }
    }
}

