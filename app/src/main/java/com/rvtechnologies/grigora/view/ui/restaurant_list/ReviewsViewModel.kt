package com.rvtechnologies.grigora.view.ui.restaurant_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.ReviewItem

class ReviewsViewModel : ViewModel() {
    var id: MutableLiveData<String> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var reviewListRes: MutableLiveData<Any> = MutableLiveData()


    fun getRestaurantsReviews() {
        isLoading.value = true
        ApiRepo.getInstance()
            .getRestaurantReviews(
                id.value.toString().trim()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<ArrayList<ReviewItem>>>() {}.type
                    reviewListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    reviewListRes.value = result
                }
            }
    }
}
