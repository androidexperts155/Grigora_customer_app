package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.LoginResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils

class SocialLoginViewModel : ViewModel() {


    var isLoading = MutableLiveData<Boolean>()
    var loginResult = MutableLiveData<Any>()


    fun login(
        name: String,
        email: String,
        phone: String,
        loginType: String,
        socialId: String
    ) {
        isLoading.value = true

        ApiRepo.getInstance()
            .socialLogin(
                name, email, phone, loginType, socialId
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    loginResult.value =
                        Gson().fromJson(
                            result as JsonElement,
                            LoginResponseModel::class.java
                        )
                } else {
                    loginResult.value = result
                }
            }

    }

    fun changeToLogin() {
        isLoading.value = true
             ApiRepo.getInstance()
                .changeToLogin()
                { success, result ->
                    isLoading.value = false
                    if (success) {

                    } else {
                    }
                }

    }


}
