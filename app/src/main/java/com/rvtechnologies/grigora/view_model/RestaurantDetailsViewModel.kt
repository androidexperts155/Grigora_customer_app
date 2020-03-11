package com.rvtechnologies.grigora.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.AddCartModel
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.RestaurantDetailModel
import com.rvtechnologies.grigora.model.models.CartDetail
import com.rvtechnologies.grigora.model.models.CommonResponseModel

class RestaurantDetailsViewModel : ViewModel() {
    var id: MutableLiveData<String> = MutableLiveData()
    var token: MutableLiveData<String> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()
    var restaurantDetail: MutableLiveData<Any> = MutableLiveData()
    var addCartRes: MutableLiveData<Any> = MutableLiveData()
    var cartItemList: MutableLiveData<Any> = MutableLiveData()

    fun getRestaurantsDetails(token: String, restId: String, price: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getRestaurantsDetails(
                token,
                restId, price
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<RestaurantDetailModel>>() {}.type
                    restaurantDetail.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    restaurantDetail.value = result
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
                    }
                }
        }
    }

    fun updateType(restaurantId: String, type: String, token: String) {
        ApiRepo.getInstance()
            .changeOrderType(
                token = token,
                restaurant_id = restaurantId,
                cart_type = type
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<*>>() {}.type
//                    addCartRes.value = Gson().fromJson(result as JsonElement, type)
                } else {
//                    addCartRes.value = result
                }
            }

    }
}
