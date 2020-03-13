package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.ReceivedVoucherModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class BuyOrRedeemViewModel : ViewModel() {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var cards: MutableLiveData<Any> = MutableLiveData()
    var redeemRes: MutableLiveData<Any> = MutableLiveData()
    var redeemResponse: MutableLiveData<Any> = MutableLiveData()

    fun getCards(token: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .getCards(
                token = token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<Collection<ReceivedVoucherModel>>>() {}.type
                    cards.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    cards.value = result
                }
            }
    }

    fun redeem(token: String, code: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .redeem(
                token = token,
                voucher_code = code
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<*>>() {}.type
                    redeemRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    redeemRes.value = result
                }
            }
    }

}
