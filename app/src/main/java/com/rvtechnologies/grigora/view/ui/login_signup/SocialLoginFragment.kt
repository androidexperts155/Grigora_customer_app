package com.rvtechnologies.grigora.view.ui.login_signup


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.models.LoginResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.PrefConstants
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view_model.SocialLoginViewModel
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.social_login_fragment.*
import org.json.JSONException
import java.lang.Exception
import java.util.*

class SocialLoginFragment : Fragment() {
    var mTwitterAuthClient: TwitterAuthClient? = null
    var session: TwitterSession? = null
    var CONSUMER_KEY = "arBGnENEuayLrERK5zdQU9E2V"
    var CONSUMER_SECRET: kotlin.String? = "NStIyaKHLuKBHmPaeoXXnxYDoaawf1vwq5UG1ZxtkJeFIsbOeS"

    var gso: GoogleSignInOptions? = null
    var intent: Intent? = null
    var callbackManager: CallbackManager? = null
    var fbLoginManager: LoginManager? = null


    companion object {
        fun newInstance() = SocialLoginFragment()
    }

    private lateinit var viewModel: SocialLoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SocialLoginViewModel::class.java)
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


        //twitter
        val config = TwitterConfig.Builder(context)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET))
            .debug(true)
            .build()
        Twitter.initialize(config)
        mTwitterAuthClient = TwitterAuthClient()

        gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().requestId()
                .build()

        intent = GoogleSignIn.getClient(activity!!, gso!!).signInIntent
        callbackManager = CallbackManager.Factory.create()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CommonUtils.changeStatusBarColor(
            ContextCompat.getColor(context!!, R.color.lightGrey),
            activity as MainActivity
        )
        return inflater.inflate(R.layout.social_login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        img_facebook.setOnClickListener {
            fbLogin()
        }
        img_twitter.setOnClickListener {
            mTwitterAuthClient!!.authorize(
                activity,
                object : Callback<TwitterSession>() {
                    override fun success(twitterSessionResult: Result<TwitterSession>) { // Success
                        session = TwitterCore.getInstance().sessionManager.activeSession
                        val authToken = session!!.authToken
                        val token = authToken.token
                        val secret = authToken.secret
                        mTwitterAuthClient!!.requestEmail(
                            session,
                            object : Callback<String?>() {
                                override fun success(emailResult: Result<String?>) {
                                    var email = emailResult.data
                                    val name = session!!.getUserName()
                                    email = session!!.getUserId().toString() + "@twitter.com"
                                    //                                        Toast.makeText(Login.this, "vvv:"+session.getUserId(), Toast.LENGTH_SHORT).show();
//                                    hitSocialLoginApi(name, email, 3, "" + session.getUserId())
                                }

                                override fun failure(e: TwitterException) {
                                    Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                        Log.e("twitterData", "" + twitterSessionResult.data)
                        //                        Toast.makeText(Login.this, "Login Success" + session.getUserName(), Toast.LENGTH_SHORT).show();
                    }

                    override fun failure(e: TwitterException) {
                        e.printStackTrace()
                    }
                })

        }
        img_google.setOnClickListener {
            googleLogin()
        }
        img_instagram.setOnClickListener {

        }

        tv_login.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_social_fragment_to_login)

        }

        img_phone.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_social_fragment_to_phone_login)
        }

        tv_signup.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_social_fragment_to_signup)

        }


        tv_skip.setOnClickListener {
            var latitude = 0.0
            if (!CommonUtils.getPrefValue(
                    context,
                    PrefConstants.LATITUDE
                ).isNullOrEmpty()
            )
                latitude =
                    CommonUtils.getPrefValue(context, PrefConstants.LATITUDE).toDouble()


            if (latitude != 0.0) {
//                view?.findNavController()
//                    ?.navigate(R.id.action_social_fragment_to_dashboardFragment)
                (activity as MainActivity).clearStack()
                (activity as MainActivity).selectedNavigation(R.id.dashBoardFragment)
            } else
                view?.findNavController()
                    ?.navigate(R.id.action_social_fragment_to_selectLocationFragment)
        }
//        scrollview.postDelayed({
//            scrollview.smoothScrollTo(0,20)
//        }, 2000)
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) {
            (activity as MainActivity).hideAll()
            (activity as MainActivity).lockDrawer(true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //this is for twitter
        mTwitterAuthClient!!.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)

        var task = GoogleSignIn.getSignedInAccountFromIntent(data);
        handleSignInResult(task)

    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            var account = completedTask.getResult(ApiException::class.java)
            viewModel.login(account?.displayName!!, account?.email!!, "", "4", account?.id!!)


            // Signed in successfully, show authenticated UI.
//        updateUI(account);
        } catch (e: Exception) {

//        updateUI(null);
        }
    }

    fun fbLogin() {

        fbLoginManager = LoginManager.getInstance()
        fbLoginManager?.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        fbLoginManager?.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {

                Log.d("Facebook Token: ", result?.accessToken?.token)
                setFacebookData(result!!)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
                Log.d("Facebook Error : ", error?.message)
            }
        })
    }

    private fun setFacebookData(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { `object`, response ->
            // Application code
            try {
                Log.i("Response", response.toString())
                val email =
                    response.jsonObject.getString("id") + "@facebook.com"
                val firstName =
                    response.jsonObject.getString("first_name")
                val lastName =
                    response.jsonObject.getString("last_name")
                //                            String gender = response.getJSONObject().getString("gender");
                val social_id = response.jsonObject.getString("id")

                Log.i("Login" + "FirstName", firstName)
                Log.i("Login" + "LastName", lastName)
                //                            Log.i("Login" + "Gender", gender);
                viewModel.login("$firstName $lastName", email, "", "3", social_id)
                //                            hitApiLoginWithSocial(firstName, lastName, "facebook_id", id, email, "", profilePic);
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name,gender")
        request.parameters = parameters
        request.executeAsync()
    }

    fun googleLogin() {
        startActivityForResult(intent, 121)
    }

    private fun saveData(data: LoginResponseModel) {
        CommonUtils.savePrefs(context, PrefConstants.TOKEN, data.tokenType + " " + data.accessToken)
        CommonUtils.savePrefs(context, PrefConstants.ID, data.data?.id?.toString())
        CommonUtils.savePrefs(context, PrefConstants.NAME, data.data?.name?.toString())
        CommonUtils.savePrefs(context, PrefConstants.IMAGE, data.data?.image?.toString())


        if (data?.data?.have_address!!) {
            view?.findNavController()?.navigate(R.id.action_social_fragment_to_addressListFragment)
        } else {
            view?.findNavController()
                ?.navigate(R.id.action_social_fragment_to_selectLocationFragment)
        }
    }


}
