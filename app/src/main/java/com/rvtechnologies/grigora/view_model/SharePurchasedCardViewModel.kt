package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.SearchUserModel
import com.rvtechnologies.grigora.model.VoucherCodeModel
import com.rvtechnologies.grigora.model.api.ApiClient
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.CommonUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SharePurchasedCardViewModel : ViewModel() {

    lateinit var callback: Call<JsonElement>
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    var sendGift: MutableLiveData<Any> = MutableLiveData()

    var searchResult: MutableLiveData<Any> = MutableLiveData()


    fun getUsers(token: String, username: String) {
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


    fun sendGift(email:String, code: String) {
        isLoading.value = true

        ApiRepo.getInstance()
            .sendGift(
                token = CommonUtils.getToken(),
                email = email,
                voucher_code = code, reference = "", type = ""
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

}
