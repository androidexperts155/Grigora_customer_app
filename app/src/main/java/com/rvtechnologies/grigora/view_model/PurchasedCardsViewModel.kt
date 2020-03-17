package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.MyPurchasedCardModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class PurchasedCardsViewModel : ViewModel() {
    val purchasedCards = MutableLiveData<Any>()
    val isLoading = MutableLiveData<Boolean>()
    var redeemRes: MutableLiveData<Any> = MutableLiveData()


    fun getPurchasedCards() {
        isLoading.value = true
        ApiRepo.getInstance()
            .getPurchasedCards { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<Collection<MyPurchasedCardModel>>>() {}.type
                    purchasedCards.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    purchasedCards.value = result
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
