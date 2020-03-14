package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.QuizModel
import com.rvtechnologies.grigora.model.WalletHistoryModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.LogoutModel
import com.rvtechnologies.grigora.utils.CommonUtils

class ProfileViewModel : ViewModel() {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var logoutRes: MutableLiveData<Any> = MutableLiveData()
    var historyResponse: MutableLiveData<Any> = MutableLiveData()

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

    fun getHistory(token: String) {
        if (CommonUtils.isLogin()) {

            isLoading.value = true

            ApiRepo.getInstance()
                .walletHistory(
                    token = token
                ) { success, result ->
                    isLoading.value = false
                    if (success) {
                        val type = object :
                            TypeToken<CommonResponseModel<WalletHistoryModel>>() {}.type
                        historyResponse.value = Gson().fromJson(result as JsonElement, type)
                    } else {
                        historyResponse.value = result
                    }
                }
        }
    }


}
