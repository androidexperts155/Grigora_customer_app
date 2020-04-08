package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.AddCartModel
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.model.models.CommonResponseModel
import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.model.models.TrendingMealsModel

class TrendingMealsViewModel : ViewModel() {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()
    var latitude: MutableLiveData<String> = MutableLiveData()
    var longitude: MutableLiveData<String> = MutableLiveData()
    var trendingResponse: MutableLiveData<Any> = MutableLiveData()
    var addCartRes: MutableLiveData<Any> = MutableLiveData()
    var cartItemList: MutableLiveData<Any> = MutableLiveData()

    fun trendingMeals() {
        isLoading.value = true

        ApiRepo.getInstance()
            .trendingMeals(
                token = token.value.toString(),
                longitude = longitude.value.toString(),
                latitude = latitude.value.toString()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object :
                        TypeToken<CommonResponseModel<TrendingMealsModel>>() {}.type
                    trendingResponse.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    trendingResponse.value = result
                }
            }
    }

    fun getCartItems(token: String, itemId: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getItemCart(
                token,
                itemId
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<ArrayList<CartDetail>>>() {}.type
                    cartItemList.value = Gson().fromJson(result as JsonElement, type)
                } else {
                    cartItemList.value = result
                }
            }
    }

    fun addItemToCart(restaurantId: String, itemId: String, price: String, quantity: String) {
        if (token.value.toString().isNotBlank()) {
            isLoading.value = true
            ApiRepo.getInstance()
                .addItemToCart(
                    token = token.value.toString(),
                    restaurantId = restaurantId,
                    itemId = itemId,
                    price = price,
                    quantity = quantity,
                    itemChoices = ""
                ) { success, result ->
                    isLoading.value = false
                    if (success) {
                        val type = object : TypeToken<CommonResponseModel<AddCartModel>>() {}.type
                        addCartRes.value = Gson().fromJson(result as JsonElement, type)
                    } else
                        addCartRes.value = result
                }
        }
    }

    fun updateCartQty(token: String, itemId: String, cartId: String, quantity: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .updateCartQty(
                token,
                itemId, quantity, cartId
            ) { success, result ->
                isLoading.value = false
                if (success) {
                }
            }
    }
}