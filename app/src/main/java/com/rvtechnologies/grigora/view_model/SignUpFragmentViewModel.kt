package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.LoginResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import java.util.regex.Pattern


class SignUpFragmentViewModel : ViewModel() {
    /*
    Params
     */
    var name: MutableLiveData<String> = MutableLiveData()
    var phone: MutableLiveData<String> = MutableLiveData()
    var email: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    var confirm_password: MutableLiveData<String> = MutableLiveData()

    /*
    Observers
     */
    var isLoading = MutableLiveData<Boolean>()
    var signUpResult = MutableLiveData<Any>()
    var checkEmail = MutableLiveData<Any>()
    var checkPhone = MutableLiveData<Any>()

    /*
    Login Method require email password
     */
    fun checkEmail() {
        ApiRepo.getInstance()
            .checkEmail(
                email.value.toString().trim()
            ) { success, result ->
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<*>>() {}.type
                    checkEmail.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    checkEmail.value = result
                }

            }
    }

    fun deleteUser(userId: String) {
        ApiRepo.getInstance()
            .deleteUser(
                userId
            ) { success, result ->
                if (success) {

                } else {

                }
            }
    }


    fun checkPhone(phone: String) {
        ApiRepo.getInstance()
            .checkPhone(
                phone
            ) { success, result ->
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<*>>() {}.type
                    checkPhone.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    checkPhone.value = result
                }

            }
    }


    fun signUp(code: String) {
        isLoading.value = true
        if (isValidData()) {
            ApiRepo.getInstance()
                .signUp(
                    name.value.toString().trim(),
                    email.value.toString().trim(),
                    code + phone.value.toString().trim(),
                    password.value.toString().trim(),
                    confirm_password.value.toString().trim()
                ) { success, result ->
                    isLoading.value = false
                    if (success) {
                        signUpResult.value =
                            Gson().fromJson(
                                result as JsonElement,
                                LoginResponseModel::class.java
                            )
                    } else {
                        signUpResult.value = result
                    }
                }
        }
    }

    /*
    Validate login credentials from user
     */
    fun isValidData(): Boolean {
        val specailCharPatten: Pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE)
        val UpperCasePatten: Pattern = Pattern.compile("[A-Z ]")
        val digitCasePatten: Pattern = Pattern.compile("[0-9 ]")


        if (name.value.isNullOrBlank()) {
            isLoading.value = false
            signUpResult.value = "Invalid name"
            return false
        } else if (email.value.isNullOrBlank() || !CommonUtils.isValidEmail(email.value.toString())) {
            isLoading.value = false
            signUpResult.value = "Invalid Email"
            return false
        } else if (password.value.isNullOrBlank() || password.value.toString().length < 6) {
            isLoading.value = false
            signUpResult.value = "Invalid Password"
            return false
        } else if (phone.value.isNullOrBlank() || phone.value.toString().length < 8 || phone.value.toString().length > 14) {
            isLoading.value = false
            signUpResult.value = "Invalid phone number"
            return false
        } else if (password.value.toString() != confirm_password.value.toString()) {
            isLoading.value = false
            signUpResult.value = "Please confirm password"
            return false
        } else
            return true
    }
}