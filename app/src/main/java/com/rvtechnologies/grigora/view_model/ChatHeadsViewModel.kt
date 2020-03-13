package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.ChatHeadModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class ChatHeadsViewModel : ViewModel() {
    var subIssuesRes = MutableLiveData<Any>()
    var isLoading = MutableLiveData<Boolean>()

    fun getChatHeads(token: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .getChatHeads(
                token = token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<ArrayList<ChatHeadModel>>>() {}.type
                    subIssuesRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    subIssuesRes.value = result
                }
            }
    }
}
