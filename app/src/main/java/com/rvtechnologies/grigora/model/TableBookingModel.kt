package com.rvtechnologies.grigora.model


import com.google.gson.annotations.SerializedName

data class TableBookingModel(
    @SerializedName("booking_status")
    var bookingStatus: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("date")
    var date: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("no_of_seats")
    var noOfSeats: String = "",
    @SerializedName("restaurant_id")
    var restaurantId: String = "",
    @SerializedName("start_time_from")
    var startTimeFrom: String = "",
    @SerializedName("start_time_to")
    var startTimeTo: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("user_id")
    var userId: Int = 0
)