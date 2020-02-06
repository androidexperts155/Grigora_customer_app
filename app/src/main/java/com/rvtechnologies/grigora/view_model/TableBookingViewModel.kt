package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.TableBookingModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.utils.ApiConstants

class TableBookingViewModel : ViewModel() {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var bookingDetail: MutableLiveData<Any> = MutableLiveData()


    fun bookTable(
        headers: HashMap<String, Any>,
        body: HashMap<String, Any?>
    ) {
        isLoading.value = true
        ApiRepo.getInstance()
            .postData(
                ApiConstants.BOOK_TABLE,
                headers, body
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<TableBookingModel>>() {}.type
                    bookingDetail.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    bookingDetail.value = result
                }
            }
    }

}
