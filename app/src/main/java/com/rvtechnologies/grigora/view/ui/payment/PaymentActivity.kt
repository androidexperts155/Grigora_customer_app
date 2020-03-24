package com.rvtechnologies.grigora.view.ui.payment

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Charge
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.databinding.ActivityPaymentBinding
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.GrigoraApp
import com.rvtechnologies.grigora.utils.PrefConstants
import java.util.*

class PaymentActivity : AppCompatActivity() {
    private var showingGray = true
    private var inSet: AnimatorSet? = null
    private var outSet: AnimatorSet? = null
    private var activitySubmitCreditCardBinding: ActivityPaymentBinding? = null
    private var card: Card? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activitySubmitCreditCardBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_payment)
        card = Card()
        setSupportActionBar(activitySubmitCreditCardBinding?.toolbar)


        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        GrigoraApp.getInstance().setCurrentActivity(this)
        updateLocale(false)

        val onHelpClickListener =
            View.OnClickListener {
                Toast.makeText(
                    this@PaymentActivity,
                    "The CVV Number (\"Card Verification Value\") is a 3 or 4 digit number on your credit and debit cards",
                    Toast.LENGTH_LONG
                ).show()
            }

        activitySubmitCreditCardBinding!!.iconHelpGray.setOnClickListener(onHelpClickListener)
        activitySubmitCreditCardBinding!!.iconHelpBlue.setOnClickListener(onHelpClickListener)

        activitySubmitCreditCardBinding!!.inputEditCardNumber.addTextChangedListener(object :
            TextWatcher {
            private var lock = false
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.isNotEmpty()) {
                    flipToBlue()
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (lock || s.length > 16) {
                    return
                }
                lock = true
                var i = 4
                while (i < s.length) {
                    if (s.toString()[i] != ' ') {
                        s.insert(i, " ")
                    }
                    i += 5
                }
                lock = false
            }
        })

        activitySubmitCreditCardBinding!!.inputEditExpiredDate.addTextChangedListener(object :
            TextWatcher {
            private var lock = false
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                if (lock || s.length > 4) {
                    return
                }
                lock = true
                if (s.length > 2 && s.toString()[2] != '/') {
                    s.insert(2, "/")
                }
                lock = false
            }
        })

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
//        val width = size.x
//        val height = size.y
//
//        val adapter: PagerAdapter = MyPagerAdapter()
//        activitySubmitCreditCardBinding!!.viewPager.adapter = adapter
//        activitySubmitCreditCardBinding!!.viewPager.clipToPadding = false
//        activitySubmitCreditCardBinding!!.viewPager.setPadding(width / 4, 0, width / 4, 0)
//        activitySubmitCreditCardBinding!!.viewPager.pageMargin = width / 14
//        activitySubmitCreditCardBinding!!.viewPager.setPagingEnabled(false)
//        activitySubmitCreditCardBinding!!.viewPager.addOnPageChangeListener(object :
//            ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {
//            }
//
//            override fun onPageScrolled(
//                position: Int,
//                positionOffset: Float,
//                positionOffsetPixels: Int
//            ) {
//            }
//
//            override fun onPageSelected(position: Int) {
//                when (position) {
//                    0 -> {
//                        updateProgressBar(25)
//                        activitySubmitCreditCardBinding!!.inputEditCardNumber.isFocusableInTouchMode =
//                            true
//                        activitySubmitCreditCardBinding!!.inputEditExpiredDate.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditCardHolder.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditCvvCode.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditCardNumber.requestFocus()
//                        return
//                    }
//                    1 -> {
//                        updateProgressBar(50)
//                        activitySubmitCreditCardBinding!!.inputEditCardNumber.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditExpiredDate.isFocusableInTouchMode =
//                            true
//                        activitySubmitCreditCardBinding!!.inputEditCardHolder.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditCvvCode.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditExpiredDate.requestFocus()
//                        return
//                    }
//                    2 -> {
//                        updateProgressBar(75)
//                        activitySubmitCreditCardBinding!!.inputEditCardNumber.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditExpiredDate.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditCardHolder.isFocusableInTouchMode =
//                            true
//                        activitySubmitCreditCardBinding!!.inputEditCvvCode.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditCardHolder.requestFocus()
//                        return
//                    }
//                    3 -> {
//                        updateProgressBar(100)
//                        activitySubmitCreditCardBinding!!.inputEditCardNumber.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditExpiredDate.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditCardHolder.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditCvvCode.isFocusableInTouchMode =
//                            true
//                        activitySubmitCreditCardBinding!!.inputEditCvvCode.requestFocus()
//                        return
//                    }
//                    4 -> {
//                        activitySubmitCreditCardBinding!!.inputEditCardNumber.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditExpiredDate.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditCardHolder.isFocusable = false
//                        activitySubmitCreditCardBinding!!.inputEditCvvCode.isFocusable = false
//                        return
//                    }
//                }
//            }
//        })

//        val onEditorActionListener =
//            OnEditorActionListener { v, actionId, event ->
//                var handled = false
//                if (actionId == EditorInfo.IME_ACTION_NEXT) {
////                    activitySubmitCreditCardBinding!!.viewPager.currentItem =
////                        activitySubmitCreditCardBinding!!.viewPager.currentItem + 1
//                    handled = true
//                }
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    submit()
//                    handled = true
//                }
//                handled
//            }

//        activitySubmitCreditCardBinding!!.inputEditCardNumber.setOnEditorActionListener(
//
//            onEditorActionListener
//
//
//        )
//        activitySubmitCreditCardBinding!!.inputEditExpiredDate.setOnEditorActionListener(
//
//            onEditorActionListener
//        )
//        activitySubmitCreditCardBinding!!.inputEditCardHolder.setOnEditorActionListener(
//            onEditorActionListener
//        )
//        activitySubmitCreditCardBinding!!.inputEditCvvCode.setOnEditorActionListener(
//            onEditorActionListener
//        )
//
//        activitySubmitCreditCardBinding?.inputEditCardNumber?.requestFocus()

        inSet = AnimatorInflater.loadAnimator(this, R.animator.card_flip_in) as AnimatorSet
        outSet = AnimatorInflater.loadAnimator(this, R.animator.card_flip_out) as AnimatorSet

        /*    card_form.cardRequired(true)
               .expirationRequired(true)
               .cvvRequired(true)
               .cardholderName(CardForm.FIELD_REQUIRED)
               .postalCodeRequired(true)
               .mobileNumberRequired(true)
               .mobileNumberExplanation("SMS is required on this number")
               .actionLabel("Purchase")
               .setup(this)*/


        activitySubmitCreditCardBinding!!.labelSecureSubmission.setOnClickListener {
            val cardNumber = "50606 66666 66666 6666"
            val expiryMonth = 11 //any month in the future
            val expiryYear = 22 // any year in the future. '2018' would work also!
            val cvv = "123" // cvv of the test card

            val card = co.paystack.android.model.Card(cardNumber, expiryMonth, expiryYear, cvv)

            if (card.isValid) {
                PaystackSdk.chargeCard(
                    this,
                    Charge().setCard(card).setAmount(35).setCurrency("NGN")
                        .setEmail("test@yopmail.com"),
                    object : Paystack.TransactionCallback {
                        override fun onSuccess(transaction: Transaction) {
                            var intent = Intent()
                            intent.putExtra("reference", transaction.reference)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }

                        override fun beforeValidate(transaction: Transaction) {
                        }

                        override fun onError(error: Throwable, transaction: Transaction) {
//handle error here
                            Toast.makeText(this@PaymentActivity, "" + error, Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
            }
        }

        /* card_form.setOnCardFormValidListener {
             if (it) {
                 card_form.cardNumber
                 card_form.expirationMonth
                 card_form.expirationYear
                 card_form.cvv
                 card_form.cardholderName
                 card_form.postalCode
                 card_form.countryCode
                 card_form.mobileNumber

                 val card = co.paystack.android.model.Card(
                     "50606 66666 66666 6666", 11
                     , 22
                     , "123"
                 )

                 PaystackSdk.chargeCard(
                     this,
                     Charge().setCard(card).setAmount(35).setCurrency("USD").setEmail("test@yopmail.com"),
                     object : Paystack.TransactionCallback {
                         override fun onSuccess(transaction: Transaction) {
                             Toast.makeText(
                                 this@PaymentActivity,
                                 "Payment Done",
                                 Toast.LENGTH_SHORT
                             )
                                 .show()
                         }

                         override fun beforeValidate(transaction: Transaction) {
 // This is called only before requesting OTP.
 // Save reference so you may send to server. If
 // error occurs with OTP, you should still verify on server.
                             Toast.makeText(
                                 this@PaymentActivity,
                                 "" + transaction.reference,
                                 Toast.LENGTH_SHORT
                             ).show()
                         }

                         override fun onError(error: Throwable, transaction: Transaction) {
 //handle error here
                             Toast.makeText(this@PaymentActivity, "" + error, Toast.LENGTH_SHORT)
                                 .show()
                         }
                     })
             }
         }*/
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

    private fun hideKeyboard(view: View) {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    private fun submit() {
//        activitySubmitCreditCardBinding!!.viewPager.currentItem = 4
        card!!.cardNumber = activitySubmitCreditCardBinding!!.inputEditCardNumber.text.toString()
        card!!.expiredDate = activitySubmitCreditCardBinding!!.inputEditExpiredDate.text.toString()
        card!!.cardHolder = activitySubmitCreditCardBinding!!.inputEditCardHolder.text.toString()
        card!!.cvvCode = activitySubmitCreditCardBinding!!.inputEditCvvCode.text.toString()
//        Toast.makeText(this@PaymentActivity, card.toString(), Toast.LENGTH_LONG).show()
        Handler().postDelayed({
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            activitySubmitCreditCardBinding!!.labelSecureSubmission.visibility = View.VISIBLE
            hideKeyboard(activitySubmitCreditCardBinding!!.inputEditCvvCode)
        }, 300)
    }

    private fun reset() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        activitySubmitCreditCardBinding!!.labelSecureSubmission.visibility = View.GONE
        flipToGray()
//        activitySubmitCreditCardBinding!!.viewPager.currentItem = 0
        activitySubmitCreditCardBinding!!.inputEditCardNumber.setText("")
        activitySubmitCreditCardBinding!!.inputEditExpiredDate.setText("")
        activitySubmitCreditCardBinding!!.inputEditCardHolder.setText("")
        activitySubmitCreditCardBinding!!.inputEditCvvCode.setText("")
        activitySubmitCreditCardBinding!!.inputEditCardNumber.requestFocus()
        showKeyboard(activitySubmitCreditCardBinding!!.inputEditCardNumber)
    }

    private fun flipToGray() {
        if (!showingGray && !outSet!!.isRunning && !inSet!!.isRunning) {
            showingGray = true
            activitySubmitCreditCardBinding!!.cardBlue.setCardElevation(0F)
            activitySubmitCreditCardBinding!!.cardGray.setCardElevation(0F)
            outSet!!.setTarget(activitySubmitCreditCardBinding!!.cardBlue)
            outSet!!.start()
            inSet!!.setTarget(activitySubmitCreditCardBinding!!.cardGray)
            inSet!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    activitySubmitCreditCardBinding!!.cardGray.cardElevation = convertDpToPixel(
                        12f,
                        this@PaymentActivity
                    )
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            inSet!!.start()
        }
    }

    private fun flipToBlue() {
        if (showingGray && !outSet!!.isRunning && !inSet!!.isRunning) {
            showingGray = false
            activitySubmitCreditCardBinding!!.cardGray.setCardElevation(0F)
            activitySubmitCreditCardBinding!!.cardBlue.setCardElevation(0F)
            outSet!!.setTarget(activitySubmitCreditCardBinding!!.cardGray)
            outSet!!.start()
            inSet!!.setTarget(activitySubmitCreditCardBinding!!.cardBlue)
            inSet!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    activitySubmitCreditCardBinding!!.cardBlue.cardElevation = convertDpToPixel(
                        12f,
                        this@PaymentActivity
                    )
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            inSet!!.start()
        }
    }

    fun convertDpToPixel(dp: Float, context: Context): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reset -> {
                reset()
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
