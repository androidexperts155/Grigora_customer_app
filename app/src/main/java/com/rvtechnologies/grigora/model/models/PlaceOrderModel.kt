package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class PlaceOrderModel(
    @SerializedName("app_fee")
    var appFee: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("delivery_address")
    var deliveryAddress: String?,
    @SerializedName("delivery_fee")
    var deliveryFee: String?,
    @SerializedName("delivery_note")
    var deliveryNote: String?,
    @SerializedName("end_lat")
    var endLat: String?,
    @SerializedName("end_long")
    var endLong: String?,
    @SerializedName("final_price")
    var finalPrice: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("order_details")
    var orderDetails: OrderDetails?,
    @SerializedName("order_status")
    var orderStatus: String?,
    @SerializedName("payment_data")
    var paymentData: String?,
    @SerializedName("payment_method")
    var paymentMethod: String?,
    @SerializedName("price_after_promo")
    var priceAfterPromo: String?,
    @SerializedName("price_before_promo")
    var priceBeforePromo: String?,
    @SerializedName("promocode")
    var promocode: String?,
    @SerializedName("quantity")
    var quantity: Int?,
    @SerializedName("restaurant_id")
    var restaurantId: Int?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("user_id")
    var userId: Int?
):Parcelable