package com.rvtechnologies.grigora.model

data class UpdateQtyModel(
    val app_fee: Int,
    val cart_details: List<CartDetailX>,
    val cart_id: Int,
    val cart_type: String,
    val created_at: String,
    val delivery_fee: String,
    val group_order: String,
    val id: Int,
    val max_per_person: Any,
    val quantity: Int,
    val restaurant_id: Int,
    val restaurant_image: String,
    val restaurant_name: String,
    val share_link: Any,
    val status: String,
    val total_price: String,
    val updated_at: String,
    val user_id: Int
)