package com.rvtechnologies.grigora.view.ui.groupCart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.rvtechnologies.grigora.model.ApiRepo
import com.rvtechnologies.grigora.model.GroupCartModel
import com.rvtechnologies.grigora.model.models.*
import com.rvtechnologies.grigora.utils.AppConstants
import com.rvtechnologies.grigora.utils.GrigoraApp
import java.lang.Exception

class GroupCartViewModel : ViewModel() {

    var isLoading = MutableLiveData<Boolean>()
    var responseCart = MutableLiveData<Any>()
    var responseClearCart = MutableLiveData<Any>()
    var cartId = MutableLiveData<String>()
    var cartData = MutableLiveData<GroupCartModel>()
    var token = MutableLiveData<String>()
    var promoId = MutableLiveData<String>()
    var paymentMode = MutableLiveData<String>()
    var clientToken = MutableLiveData<Any>()
    var reference = MutableLiveData<String>()
    var deliveryAddress = MutableLiveData<String>()
    var deliveryLat = MutableLiveData<String>()
    var deliveryLong = MutableLiveData<String>()
    var deliveryNote = MutableLiveData<String>()
    var cartItemList: MutableLiveData<Any> = MutableLiveData()

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


    var offerModel = MutableLiveData<OfferModel>()

    fun select(item: OfferModel) {
        offerModel.value = item
    }

    fun getSelected(): OfferModel? {
        return offerModel.value
    }

    init {
        promoId.value = "0"
        deliveryNote.value = ""
    }

    var responsePlaceOrder = MutableLiveData<Any>()
    var offersListRes: MutableLiveData<Any> = MutableLiveData()

    fun viewGroupCart(token: String, lat: String, lng: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .viewGroupCart(token, lat, lng, cartId.value.toString()) { success, result ->
                isLoading.value = false
                if (success) {
                    try {
                        val type = object : TypeToken<CommonResponseModel<GroupCartModel>>() {}.type
                        responseCart.value =
                            Gson().fromJson(
                                result as JsonElement, type
                            )

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    responseCart.value = result
                }
            }
    }

    fun placeOrderNow(cartType: String) {
        val cartData = cartData.value
        isLoading.value = true
        ApiRepo.getInstance()
            .placeOrder(
                token = token.value.toString(),
                cart_id = cartData?.id.toString(),
                promo_id = promoId.value.toString(),
                app_fee = cartData?.app_fee.toString(),
                delivery_fee = cartData?.delivery_fee.toString(),
                price_before_promo = cartData?.beforePromo.toString(),
                price_after_promo = cartData?.afterPromo.toString(),
                final_price = cartData?.cartTotal.toString(),
                payment_method = paymentMode.value.toString(),
                reference = reference.value!!,
                delivery_address = deliveryAddress.value.toString(),
                delivery_lat = deliveryLat.value.toString(),
                delivery_long = deliveryLong.value.toString(),
                delivery_note = deliveryNote.value.toString(),
                carttype = cartType

            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type = object : TypeToken<CommonResponseModel<PlaceOrderModel>>() {}.type
                    responsePlaceOrder.value =
                        Gson().fromJson(
                            result as JsonElement, type
                        )

                } else {
                    responsePlaceOrder.value = result
                }
            }
    }


    fun isFrench(): Boolean {
        return GrigoraApp.getInstance().getCurrentLanguage() == AppConstants.FRENCH
    }

    fun getOffers(restId: String) {
        isLoading.value = true
        ApiRepo.getInstance()
            .getOffers(
                token.value.toString(), restId
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<ArrayList<OfferModel>>>() {}.type
                    offersListRes.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    offersListRes.value = result
                }
            }
    }

    fun clearCart() {
        isLoading.value = true
        ApiRepo.getInstance()
            .clearCart(
                token.value.toString(), cartData.value?.id.toString()
            ) { success, result ->
                isLoading.value = false
                if (success) {
                    val type =
                        object : TypeToken<CommonResponseModel<Any>>() {}.type
                    responseClearCart.value = Gson().fromJson(result as JsonElement, type)

                } else {
                    responseClearCart.value = result
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
