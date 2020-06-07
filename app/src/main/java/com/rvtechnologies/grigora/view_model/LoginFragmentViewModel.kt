package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.R
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.LoginResponseModel
import com.rvtechnologies.grigora.model.models.User
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.utils.GrigoraApp


class LoginFragmentViewModel : ViewModel() {
    /*
    Params
     */
    var email: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()

    /*
    Observers
     */
    var isLoading = MutableLiveData<Boolean>()
    var loginResult = MutableLiveData<Any>()

    /*
    Login Method require email password
     */
    fun login() {
        isLoading.value = true
        if (isValidData()) {
            ApiRepo.getInstance()
                .login(
                    email.value.toString().trim(),
                    password.value.toString().trim()
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
    }


    /*
    Validate login credentials from user
     */
    private fun isValidData(): Boolean {


        if (email.value.isNullOrBlank() || !CommonUtils.isValidEmail(email.value.toString())) {
            isLoading.value = false
            loginResult.value = GrigoraApp.getInstance().activity?.getString(R.string.invalid_email)
            return false
        } else if (password.value.isNullOrBlank()) {
            isLoading.value = false
            loginResult.value = GrigoraApp.getInstance().activity?.getString(R.string.please_fill_password)
            return false
        } else
            return true
    }

    fun phoneLogin(code: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .checkPhone(
                code + email.value.toString().trim()
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


}