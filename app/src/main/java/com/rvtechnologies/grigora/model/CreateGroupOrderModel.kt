package com.rvtechnologies.grigora.model

data class CreateGroupOrderModel(
    val cart_type: String,
    val created_at: String,
    val group_order: String,
    val id: Int,
    val max_per_person: String,
    val quantity: Int,
    val restaurant_id: Int,
    val share_link: String,
    val status: String,
    val total_price: String,
    val updated_at: String,
    val restaurant_name: String,
    val user_id: Int
)