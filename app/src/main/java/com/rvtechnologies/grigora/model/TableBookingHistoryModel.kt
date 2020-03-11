package com.rvtechnologies.grigora.model

data class TableBookingHistoryModel(
    val booking_status: String,
    val cancel_reason: Any,
    val created_at: String,
    val customer_french_name: String,
    val customer_name: String,
    val date: String,
    val id: Int,
    val no_of_seats: Int,
    val restaurant_french_name: String,
    val restaurant_id: Int,
    val restaurant_address: String,
    val restaurant_image: String,
    val restaurant_name: String,
    val start_time_from: String,
    val start_time_to: String,
    val updated_at: String,
    val user_id: Int
)