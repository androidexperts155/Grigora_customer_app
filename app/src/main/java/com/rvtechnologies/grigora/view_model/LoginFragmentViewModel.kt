package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.LoginResponseModel
import com.rvtechnologies.grigora.model.models.User
import com.rvtechnologies.grigora.utils.CommonUtils


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


    fun changeToLogin() {
        isLoading.value = true
        if (isValidData()) {
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

    /*
    Validate login credentials from user
     */
    private fun isValidData(): Boolean {
        if (email.value.isNullOrBlank() || !CommonUtils.isValidEmail(email.value.toString())) {
            isLoading.value = false
            loginResult.value = "Invalid Email"
            return false
        } else if (password.value.isNullOrBlank()) {
            isLoading.value = false
            loginResult.value = "Invalid Password"
            return false
        } else
            return true
    }

    fun phoneLogin() {
        isLoading.value = true
        if (isValidPhone()) {
            ApiRepo.getInstance()
                .phoneLogin(
                    email.value.toString().trim()
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

    fun isValidPhone(): Boolean {
        return if (email.value.isNullOrBlank()) {
            isLoading.value = false
            loginResult.value = "Invalid Phone"
            false
        } else if (email.value.toString().length < 9) {
            isLoading.value = false
            loginResult.value = "Invalid Phone"
            false
        } else
            true
    }


}