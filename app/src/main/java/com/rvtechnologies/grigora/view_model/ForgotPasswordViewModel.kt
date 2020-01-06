package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils

class ForgotPasswordViewModel: ViewModel() {
    var email: MutableLiveData<String> = MutableLiveData()

    var isLoading = MutableLiveData<Boolean>()
    var forgotPasswordResult = MutableLiveData<Any>()



    fun forgotPassword() {
        isLoading.value = true
        if (CommonUtils.isValidEmail(email.value.toString())) {
            ApiRepo.getInstance()
                .forgotPassword(
                    email.value.toString().trim()
                ) { success, result ->
                    isLoading.value = false
                    if (success) {
                        forgotPasswordResult.value =
                            Gson().fromJson(
                                result as JsonElement,
                                CommonResponseModel::class.java
                            )
                    } else {
                        forgotPasswordResult.value = result
                    }
                }
        }
    }


}