package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.SearchUserModel
import com.rvtechnologies.grigora.model.TransferMoneyModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class SendMoneyViewModel : ViewModel() {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()
     var transferResponse: MutableLiveData<Any> = MutableLiveData()


    fun transfer(amount: String, email: String, reason: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .transferMoney(
                email = email,
                token = token.value.toString(),
                reason = reason,
                amount = amount
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<ArrayList<TransferMoneyModel>>>() {}.type
                    transferResponse.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    transferResponse.value = result
                }
            }
    }
}
