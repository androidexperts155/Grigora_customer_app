package com.rvtechnologies.grigora.view.ui.table_booking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.TableBookingHistoryModel
import com.rvtechnologies.grigora.model.TableBookingModel
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class TableBookingHistoryViewModel : ViewModel() {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var tableBookingList: MutableLiveData<Any> = MutableLiveData()


    fun getList(
        header: String
    ) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getBookedTables(
                header
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object :
                            TypeToken<CommonResponseModel<Collection<TableBookingHistoryModel>>>() {}.type
                    tableBookingList.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    tableBookingList.value = result
                }
            }
    }
}
