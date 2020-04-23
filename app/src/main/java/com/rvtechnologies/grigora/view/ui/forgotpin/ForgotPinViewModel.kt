package com.rvtechnologies.grigora.view.ui.forgotpin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.NotificationsModel
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils

class ForgotPinViewModel : ViewModel() {
    var email: MutableLiveData<String> = MutableLiveData()

    var isLoading = MutableLiveData<Boolean>()
    var forgotPasswordResult = MutableLiveData<Any>()

    fun forgotPassword() {
        isLoading.value = true
        if (CommonUtils.isValidEmail(email.value.toString())) {
            ApiRepo.getInstance()
                .forgotPin(
                    email.value.toString().trim()
                ) { success, result ->
                    isLoading.value = false
                    if (success) {

                        val type = object :
                            TypeToken<CommonResponseModel<ArrayList<NotificationsModel>>>() {}.type
                        forgotPasswordResult.value = Gson().fromJson(result as JsonElement, type)


                    } else {
                        forgotPasswordResult.value = result
                    }
                }
        } else
            isLoading.value = false

    }
}
