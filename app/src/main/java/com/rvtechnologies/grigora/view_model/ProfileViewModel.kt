package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.QuizModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.LogoutModel

class ProfileViewModel : ViewModel() {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var logoutRes: MutableLiveData<Any> = MutableLiveData()

    fun logout(token: String, deviceToken: String) {
        isLoading.value = true

        ApiRepo.getInstance().logout(
            token = token,
            deviceToken = deviceToken
        ) { success, result ->
            isLoading.value = false
            if (success) {
                logoutRes.value = true

            } else {
                logoutRes.value = result
            }
        }
    }
}
