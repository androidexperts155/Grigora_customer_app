package com.rvtechnologies.grigora.view.ui.login_signup

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.utils.PrefConstants
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.*
import java.util.concurrent.TimeUnit


class OtpActivity : AppCompatActivity(), View.OnClickListener {
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    override fun onCreate(savedInstanceState: Bundle?) {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        updateLocale(false)

        setTheme(CommonUtils.getBooleanPrefValue(this, PrefConstants.IS_DARK_MODE))
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

            )
        }
        CommonUtils.changeStatusBarColor(ContextCompat.getColor(this, R.color.textBlack), this)

        setContentView(R.layout.activity_otp)


        otp_view.setOtpCompletionListener { it ->
            Log.e("Completed", it)
        }

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }

        // Assign click listeners
        buttonVerifyPhone.setOnClickListener(this)
        buttonResend.setOnClickListener(this)

        // [START initial
        //
        //
        // ize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        auth.currentUser != null
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                // [START_EXCLUDE silent]
                verificationInProgress = false
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential)
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                CommonUtils.hideLoader()

                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                // [START_EXCLUDE silent]
                verificationInProgress = false
                // [END_EXCLUDE]

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]

                    var i = Intent().putExtra("message", "Invalid phone number.")
                    setResult(RESULT_OK, i);
                    finish();

                                   // [END_EXCLUDE]
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]

                    var i = Intent().putExtra("message", "Quota exceeded.")
                    setResult(RESULT_OK, i);
                    finish();

                    // [END_EXCLUDE]
                }
                else{
                    var i = Intent().putExtra("message", e.message.toString())
                    setResult(RESULT_OK, i);
                    finish();
                }

                // Show a message and update the UI
                // [START_EXCLUDE]



//                updateUI(STATE_VERIFY_FAILED)
                // [END_EXCLUDE]
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT)
                // [END_EXCLUDE]
            }
        }
        // [END phone_auth_callbacks]
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)

        // [START_EXCLUDE]
        if (!verificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(intent.getStringExtra("phone"))
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, verificationInProgress)
    }

    fun setTheme(isDark: Boolean) {
        if (isDark)
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme_Light)
    }

    fun updateLocale(shouldRecreate: Boolean) {
        val languageToLoad: String
        languageToLoad = if (CommonUtils.getBooleanPrefValue(
                this,
                PrefConstants.IS_LANGUAGE_SELECTED
            ) && GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH
        ) {
            "fr"
        } else
            "en"

        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()

        config.setLocale(locale)
        resources?.updateConfiguration(
            config,
            resources?.displayMetrics
        )
        if (shouldRecreate)
            recreate()


    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        verificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS)
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks
        ) // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        verificationInProgress = true
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }

    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks, // OnVerificationStateChangedCallbacks
            token
        ) // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                CommonUtils.hideLoader()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    // [START_EXCLUDE]
                    updateUI(STATE_SIGNIN_SUCCESS, user)
                    // [END_EXCLUDE]
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        // [START_EXCLUDE silent]
                        otp_view.error = "Invalid code."
                        // [END_EXCLUDE]
                    }
                    // [START_EXCLUDE silent]
                    // Update UI
                    updateUI(STATE_SIGNIN_FAILED)
                    // [END_EXCLUDE]
                }
            }
    }
    // [END sign_in_with_phone]

    private fun signOut() {
        auth.signOut()
        updateUI(STATE_INITIALIZED)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user)
        } else {
            updateUI(STATE_INITIALIZED)
        }
    }

    private fun updateUI(uiState: Int, cred: PhoneAuthCredential) {
        updateUI(uiState, null, cred)
    }

    private fun updateUI(
        uiState: Int,
        user: FirebaseUser? = auth.currentUser,
        cred: PhoneAuthCredential? = null
    ) {
        when (uiState) {
            STATE_INITIALIZED -> {
                CommonUtils.showLoader(this, "Sending OTP")
            }
            STATE_CODE_SENT -> {
                CommonUtils.hideLoader()
            }
            STATE_VERIFY_FAILED -> {
                CommonUtils.hideLoader()

//                val intent = Intent()
//                intent.putExtra("verified", false)
//                setResult(AppConstants.OTP_CODE, intent)
//                finish() //finishing a

            }
            STATE_VERIFY_SUCCESS -> {
                CommonUtils.hideLoader()

                // Verification has succeeded, proceed to firebase sign in
//                val intent = Intent()
//                intent.putExtra("verified", true)
//                setResult(AppConstants.OTP_CODE, intent)
//                finish()
//                // Set the verification text based on the credential
//                if (cred != null) {
//                    if (cred.smsCode != null) {
//                        otp_view.setText(cred.smsCode)
//                    } else {
//                        otp_view.setText("Instant Validation")
//                    }
//                }
            }
            STATE_SIGNIN_FAILED -> {
            }
            // No-op, handled by sign-in check
            STATE_SIGNIN_SUCCESS -> {
                CommonUtils.hideLoader()
                val intent = Intent()
                intent.putExtra("verified", true)
                setResult(AppConstants.OTP_CODE, intent)
                finish() //finishing a
            }

        } // Np-op, handled by sign-in check


    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = intent.getStringExtra("phone")
        if (TextUtils.isEmpty(phoneNumber)) {
            return false
        }

        return true
    }

    override fun onClick(view: View) {
        when (view.id) {

            R.id.buttonVerifyPhone -> {
                val code = otp_view.text.toString()
                CommonUtils.showLoader(this, "Verifying")
                if (TextUtils.isEmpty(code)) {
                    otp_view.error = "Cannot be empty."
                    return
                }

                verifyPhoneNumberWithCode(storedVerificationId, code)
            }
            R.id.buttonResend -> resendVerificationCode(
                intent.getStringExtra("phone"),
                resendToken
            )
        }
    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
        private const val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"
        private const val STATE_INITIALIZED = 1
        private const val STATE_VERIFY_FAILED = 3
        private const val STATE_VERIFY_SUCCESS = 4
        private const val STATE_CODE_SENT = 2
        private const val STATE_SIGNIN_FAILED = 5
        private const val STATE_SIGNIN_SUCCESS = 6
    }
}