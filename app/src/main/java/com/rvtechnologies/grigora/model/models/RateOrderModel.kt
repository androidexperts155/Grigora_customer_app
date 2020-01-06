package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class RateOrderModel(
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("order_id")
    var orderId: String?,
    @SerializedName("rating")
    var rating: String?,
    @SerializedName("receiver_id")
    var receiverId: String?,
    @SerializedName("receiver_type")
    var receiverType: String?,
    @SerializedName("review")
    var review: String?,
    @SerializedName("sender_id")
    var senderId: Int?,
    @SerializedName("updated_at")
    var updatedAt: String?
) : Parcelable