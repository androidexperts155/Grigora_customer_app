package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.FaqModel
import com.rvtechnologies.grigora.model.MessageModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel1

class ChatViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()
    var ticketId = MutableLiveData<String>()
    var issueId = MutableLiveData<String>()
    var subIssueId = MutableLiveData<String>()
    var token = MutableLiveData<String>()


    var sendMessageRes = MutableLiveData<Any>()
    var messages = MutableLiveData<Any>()

    fun sendMessage(message: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .sendMessage(
                token = token.value.toString(),
                issueId = issueId.value.toString(),
                subIssueId = subIssueId.value.toString(),
                ticketId = ticketId.value.toString(), message = message
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<MessageModel>>() {}.type
                    sendMessageRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    sendMessageRes.value = result
                }
            }
    }


    fun getChat() {
        isLoading.value = true

        ApiRepo.getInstance()
            .getChat(
                token = token.value.toString(), tiketId = ticketId.value.toString()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel1<Collection<MessageModel>>>() {}.type
                    messages.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    messages.value = result
                }
            }
    }


}
