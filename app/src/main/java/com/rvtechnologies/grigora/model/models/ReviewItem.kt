package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ReviewItem(
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("order_id")
    var orderId: Int?,
    @SerializedName("rating")
    var rating: Float?,
    @SerializedName("receiver_id")
    var receiverId: Int?,
    @SerializedName("receiver_type")
    var receiverType: String?,
    @SerializedName("review")
    var review: String?,
    @SerializedName("sender_id")
    var senderId: Int?,
    @SerializedName("sender_name")
    var senderName: String?,
    @SerializedName("updated_at")
    var updatedAt: String?
) : Parcelable