package com.rvtechnologies.grigora.view.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.braintreepayments.cardform.view.CardForm
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.ApiRepo
import kotlinx.android.synthetic.main.activity_payment.*

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        card_form.cardRequired(true)
            .expirationRequired(true)
            .cvvRequired(true)
            .cardholderName(CardForm.FIELD_REQUIRED)
            .postalCodeRequired(true)
            .mobileNumberRequired(true)
            .mobileNumberExplanation("SMS is required on this number")
            .actionLabel("Purchase")
            .setup(this)


        btn_add.setOnClickListener {

            card_form.cardNumber
            card_form.expirationMonth
            card_form.expirationYear
            card_form.cvv
            card_form.cardholderName
            card_form.postalCode
            card_form.countryCode
            card_form.mobileNumber

            val cardNumber = "50606 66666 66666 6666"
            val expiryMonth = 11 //any month in the future
            val expiryYear = 22 // any year in the future. '2018' would work also!
            val cvv = "123" // cvv of the test card

            val card = Card(cardNumber, expiryMonth, expiryYear, cvv)


            PaystackSdk.chargeCard(
                this,
                Charge().setCard(card).setAmount(35).setCurrency("NGN").setEmail("test@yopmail.com"),
                object : Paystack.TransactionCallback {
                    override fun onSuccess(transaction: Transaction) {
                        var intent = Intent()
                        intent.putExtra("reference", transaction.reference)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
//                        placeOrder(transaction.reference)
//                        Toast.makeText(
//                            activity,
//                            "Payment Done",
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
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

        card_form.setOnCardFormValidListener {
            if (it) {
                card_form.cardNumber
                card_form.expirationMonth
                card_form.expirationYear
                card_form.cvv
                card_form.cardholderName
                card_form.postalCode
                card_form.countryCode
                card_form.mobileNumber

                val card = Card(
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
        }

    }


}
