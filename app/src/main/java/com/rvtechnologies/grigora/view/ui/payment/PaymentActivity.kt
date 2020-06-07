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
import com.whiteelephant.monthpicker.MonthPickerDialog
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

        activitySubmitCreditCardBinding!!.inputLayoutExpiredDate.setOnClickListener {
            monthYearPicker()
        }

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        inSet = AnimatorInflater.loadAnimator(this, R.animator.card_flip_in) as AnimatorSet
        outSet = AnimatorInflater.loadAnimator(this, R.animator.card_flip_out) as AnimatorSet


        activitySubmitCreditCardBinding!!.labelSecureSubmission.setOnClickListener {
            if (activitySubmitCreditCardBinding!!.inputEditCardNumber.text.toString()
                    .isNullOrEmpty()
            ) {
                CommonUtils.showMessage(
                    activitySubmitCreditCardBinding!!.parent,
                    getString(R.string.please_enter_card_number)
                )

            } else if (activitySubmitCreditCardBinding!!.inputEditCardNumber.text.toString().length < 16) {
                CommonUtils.showMessage(
                    activitySubmitCreditCardBinding!!.parent,
                    getString(R.string.invalidCardnumber)
                )
            } else if (
                activitySubmitCreditCardBinding!!.inputEditExpiredDate.text.toString().length < 5
            ) {
                CommonUtils.showMessage(
                    activitySubmitCreditCardBinding!!.parent,
                    getString(R.string.invalid_date)
                )

            } else if (activitySubmitCreditCardBinding!!.inputEditCvvCode.text.toString()
                    .isNullOrEmpty()
            ) {

                CommonUtils.showMessage(
                    activitySubmitCreditCardBinding!!.parent,
                    getString(R.string.please_enter_cvv)
                )
            } else if (
                activitySubmitCreditCardBinding!!.inputEditCvvCode.text.toString().length < 3
            ) {
                CommonUtils.showMessage(
                    activitySubmitCreditCardBinding!!.parent,
                    getString(R.string.invalidcvv)
                )

            } else {
                val cardNumber =
                    activitySubmitCreditCardBinding!!.inputEditCardNumber.text.toString()
                val expiryMonth =
                    activitySubmitCreditCardBinding!!.inputEditExpiredDate.text.toString()
                        .substring(
                            0,
                            activitySubmitCreditCardBinding!!.inputEditExpiredDate.text.toString()
                                .lastIndexOf('/')
                        ) //any month in the future
                val expiryYear =
                    activitySubmitCreditCardBinding!!.inputEditExpiredDate.text.toString()
                        .substring(
                            activitySubmitCreditCardBinding!!.inputEditExpiredDate.text.toString()
                                .lastIndexOf('/') + 1,
                            activitySubmitCreditCardBinding!!.inputEditExpiredDate.text.toString().length
                        ) //any month in the future
                val cvv =
                    activitySubmitCreditCardBinding!!.inputEditCvvCode.text.toString()// cvv of the test card

                val card = co.paystack.android.model.Card(
                    cardNumber,
                    expiryMonth.toInt(),
                    expiryYear.toIntOrNull(),
                    cvv
                )






                if (card.isValid) {
                    CommonUtils.showLoader(
                        this@PaymentActivity!!,
                        ""
                    )
                    PaystackSdk.chargeCard(
                        this,
                        Charge().setCard(card).setAmount((intent.getDoubleExtra("amount", 0.0) * 100).toInt())
                            .setCurrency("NGN")
                            .setEmail(CommonUtils.getPrefValue(this, PrefConstants.EMAIL)),
                        object : Paystack.TransactionCallback {
                            override fun onSuccess(transaction: Transaction) {
                                CommonUtils.hideLoader(

                                )
                                var intent = Intent()
                                intent.putExtra("reference", transaction.reference)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }

                            override fun beforeValidate(transaction: Transaction) {

                            }

                            override fun onError(error: Throwable, transaction: Transaction) {
                                CommonUtils.hideLoader(

                                )
                                //handle error here
                                Toast.makeText(this@PaymentActivity, "" + error, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        })
                } else {
                    CommonUtils.showMessage(
                        activitySubmitCreditCardBinding!!.parent,
                        getString(R.string.invalidCard)
                    )
                }
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

    private fun reset() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//        activitySubmitCreditCardBinding!!.labelSecureSubmission.visibility = View.GONE
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

    private fun monthYearPicker() {

        var today = Calendar.getInstance()

        var builder = MonthPickerDialog.Builder(this, object : MonthPickerDialog.OnDateSetListener {
            override fun onDateSet(selectedMonth: Int, selectedYear: Int) {


                var month = if (selectedMonth + 1 < 10) {
                    "0${selectedMonth + 1}"
                } else
                    (selectedMonth + 1).toString()

                var year = selectedYear.toString()
                    .substring(
                        selectedYear.toString().length - 2,
                        selectedYear.toString().length
                    )

                activitySubmitCreditCardBinding!!.inputEditExpiredDate.setText("$month/$year")
            }

        }, today.get(Calendar.YEAR), today.get(Calendar.MONTH)).setMaxYear(2040)
        builder.setMinYear(today.get(Calendar.YEAR))
            .setTitle(getString(R.string.set_ending_time)).build().show()

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
