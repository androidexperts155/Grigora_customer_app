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

class WalletViewModel : ViewModel() {

    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()
    var historyResponse: MutableLiveData<Any> = MutableLiveData()
    var addMoneyResponse: MutableLiveData<Any> = MutableLiveData()

    fun getHistory() {
        isLoading.value = true

        ApiRepo.getInstance()
            .walletHistory(
                token = token.value.toString()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    historyResponse.value =
                        Gson().fromJson(result as JsonElement, WalletHistoryModel::class.java)
                } else {
                    historyResponse.value = result
                }
            }
    }

    fun addMoney(tranactionData: String, amount: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .addMoney(
                token = token.value.toString(), transactionData = tranactionData, amount = amount
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    addMoneyResponse.value =
                        Gson().fromJson(result as JsonElement, AddMoneyModel::class.java)
                } else {
                    addMoneyResponse.value = result
                }
            }
    }

}
