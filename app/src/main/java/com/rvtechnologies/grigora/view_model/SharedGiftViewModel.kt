package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.*
import com.rvtechnologies.grigora.model.api.ApiClient
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import com.rvtechnologies.grigora.view.ui.MainActivity
import com.rvtechnologies.grigora.view.ui.profile.wallet.GiftFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SharedGiftViewModel : ViewModel() {
    lateinit var callback: Call<JsonElement>
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var searchResult: MutableLiveData<Any> = MutableLiveData()
    var voucherCodes: MutableLiveData<Any> = MutableLiveData()
    var voucherCode: MutableLiveData<Any> = MutableLiveData()
    var sendGift: MutableLiveData<Any> = MutableLiveData()
    var selectedUser: MutableLiveData<SearchUserModel> = MutableLiveData()
    var selectedVoucher: MutableLiveData<VoucherModel> = MutableLiveData()
    var isSelfSelected: MutableLiveData<Boolean> = MutableLiveData()

    fun destroy(activity: MainActivity) {

        searchResult.value = null
        voucherCodes.value = null
        voucherCode.value = null
        sendGift.value = null
        selectedUser.value = null
        selectedVoucher.value = null
        isSelfSelected.value = null

        searchResult.removeObservers(activity)
        voucherCodes.removeObservers(activity)
        voucherCode.removeObservers(activity)
        sendGift.removeObservers(activity)
        selectedUser.removeObservers(activity)
        selectedVoucher.removeObservers(activity)
        isSelfSelected.removeObservers(activity)


    }


    fun getVoucherCode(token: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .getVoucherCode(
                token = token,
                id = selectedVoucher.value?.id.toString()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<VoucherCodeModel>>() {}.type
                    voucherCode.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    voucherCode.value = result
                }
            }
    }


    fun sendGift(token: String, item: VoucherCodeModel) {
        isLoading.value = true

        ApiRepo.getInstance()
            .sendGift(
                token = token,
                email = selectedUser.value?.email.toString(),
                voucher_code = item.voucher_card_code.voucher_code
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<*>>() {}.type
                    sendGift.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    sendGift.value = result
                }
            }
    }

    fun buyCard(token: String, item: VoucherCodeModel) {
        isLoading.value = true

        ApiRepo.getInstance()
            .buyCard(
                token = token,
                voucher_code = item.voucher_card_code.voucher_code
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<*>>() {}.type
                    sendGift.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    sendGift.value = result
                }
            }
    }


    fun getVoucherCodes(token: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .getVoucherCodes(
                token = token
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<Collection<VoucherModel>>>() {}.type
                    voucherCodes.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    voucherCodes.value = result
                }
            }
    }

    fun getUsers(token: String, username: String) {
//        isLoading.value = true
        searchUsers(
            token,
            username

        ) { success, result ->
//            isLoading.value = false
            if (success) {
                val type =
                    object : TypeToken<CommonResponseModel<Collection<SearchUserModel>>>() {}.type
                searchResult.value = Gson().fromJson(result as JsonElement, type)

            } else {
                searchResult.value = result
            }
        }
    }


    private fun searchUsers(
        token: String,
        username: String,
        onResult: (isSuccess: Boolean, response: Any?) -> Unit
    ) {
        if (::callback.isInitialized) {
            callback.cancel()
        }

        callback = ApiClient.getClient().searchUser(
            token = token,
            username = username
        )

        callback.enqueue(object : Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>?,
                response: Response<JsonElement>?
            ) {
                if (response != null && response.isSuccessful)
                    onResult(true, response.body()!!)
                else {
                    onResult(false, CommonUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {
                onResult(false, t?.message)
            }
        })
    }


}
