package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class CartDataModel(
    @SerializedName("app_fee")
    var appFee: String?,

    @SerializedName("estimated_preparing_time")
    var estimated_preparing_time: String?,

    @SerializedName("estimated_delivery_time")
    var estimated_delivery_time: String?,




    @SerializedName("cart_details")
    var cartDetails: ArrayList<CartDetail?>?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("delivery_fee")
    var deliveryFee: String?,
    @SerializedName("id")
    var id: String?,
    @SerializedName("quantity")
    var quantity: String?,
    @SerializedName("restaurant_id")
    var restaurantId: String?,
    @SerializedName("restaurant_image")
    var restaurantImage: String?="",
    @SerializedName("restaurant_name")
    var restaurantName: String?,
    @SerializedName("total_price")
    var totalPrice: String?,
    @SerializedName("updated_at")

    var updatedAt: String?,
    @SerializedName("user_id")
    var userId: String?,
    var cartTotal: String?,
    var cartSubTotal: String?,
    var beforePromo: Double?,
    var afterPromo: Double?
):Parcelable