package com.rvtechnologies.grigora.view.ui.login_signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.LoginResponseModel

class OtpViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var verifyOtpRes = MutableLiveData<Any>()
    var resendOtpRes = MutableLiveData<Any>()


    fun resend(uid: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .resendOtp(
                uid
            ) { success, result ->
                isLoading.value = false

                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<*>>() {}.type
                    resendOtpRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    resendOtpRes.value = result
                }

            }
    }

    fun verifyOtp(uid: String, otp: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .verifyOtp(
                uid, otp
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    verifyOtpRes.value = Gson().fromJson(
                        result as JsonElement,
                        LoginResponseModel::class.java
                    )

                } else {
                    verifyOtpRes.value = result
                }

            }
    }


}
