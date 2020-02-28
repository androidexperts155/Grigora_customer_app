package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.AddMoneyModel
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.TransferMoneyModel
import com.rvtechnologies.grigora.model.WalletHistoryModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class TransferMoneyViewModel : ViewModel() {

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()
    var historyResponse: MutableLiveData<Any> = MutableLiveData()
    var transferResponse: MutableLiveData<Any> = MutableLiveData()


    fun getHistory() {
        isLoading.value = true

        ApiRepo.getInstance()
            .walletHistory(
                token = token.value.toString()
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


    fun transfer(amount: String, walletId: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .transferMoney(
                walletId = walletId,
                token = token.value.toString(),
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


