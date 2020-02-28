package com.rvtechnologies.grigora.model

data class GroupOrdersModel(
    val cart_items_count: Int,
    val cart_type: String,
    val cart_users_count: Int,
    val created_at: String,
    val group_order: String,
    val id: Int,
    val max_per_person: Int,
    val quantity: Int,
    val restaurant_french_name: String,
    val restaurant_id: Int,
    val restaurant_name: String,
    val share_link: String,
    val status: String,
    val total_price: String,
    val updated_at: String,
    val user_french_name: String,
    val user_id: Int,
    val user_name: String
)