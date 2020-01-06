package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class ChangePasswordViewModel : ViewModel() {

    var token = MutableLiveData<String>()
    var oldPassword = MutableLiveData<String>()
    var newPassword = MutableLiveData<String>()
    var confirmPassword = MutableLiveData<String>()

    var isLoading= MutableLiveData<Boolean>()
    var changePasswordRes= MutableLiveData<Any>()


    fun changePassword() {
        if (newPassword.value.toString().equals(confirmPassword.value.toString())) {
            isLoading.value = true
            ApiRepo.getInstance()
                .changePassword(
                    token.value.toString(),
                    oldPassword.value.toString(),
                    newPassword.value.toString()
                ) { success, result ->
                    isLoading.value = false
                    if (success) {
                        try {

                            changePasswordRes.value =
                                Gson().fromJson(
                                    result as JsonElement, CommonResponseModel::class.java
                                )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        changePasswordRes.value = result
                    }
                }
        }
        else{
            changePasswordRes.value="Password does not match"
        }
    }

}
