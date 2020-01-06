package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class OrderStatusModel(
    @SerializedName("dishRemainingTime")
    var dishRemainingTime: Int?,
    @SerializedName("distance")
    var distance: Int?,
    @SerializedName("driverTime")
    var driverTime: Int?,
    @SerializedName("end_lat")
    var endLat: Double?,
    @SerializedName("end_long")
    var endLong: Double?,
    @SerializedName("latitude")
    var latitude: Double?,
    @SerializedName("longitude")
    var longitude: Double?,
    @SerializedName("order_status")
    var orderStatus: Int?,
    @SerializedName("preparing_end_time")
    var preparingEndTime: String?,
    @SerializedName("preparing_time")
    var preparingTime: Int?
) : Parcelable