package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class OrderDetails(
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("id")
    var id: String?,
    @SerializedName("item_choices")
    var itemChoices: String?,
    @SerializedName("item_id")
    var itemId: Int?,
    @SerializedName("order_id")
    var orderId: Int?,
    @SerializedName("price")
    var price: String?,
    @SerializedName("quantity")
    var quantity: Int?,
    @SerializedName("updated_at")
    var updatedAt: String?
):Parcelable