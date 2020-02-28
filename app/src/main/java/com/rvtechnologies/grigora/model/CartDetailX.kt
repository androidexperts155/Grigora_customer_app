package com.rvtechnologies.grigora.model

data class CartDetailX(
    val cart_id: Int,
    val created_at: String,
    val id: Int,
    val item_choices: String,
    val item_french_name: String,
    val item_id: Int,
    val item_name: String,
    val price: String,
    val quantity: Int,
    val updated_at: String,
    val user_id: Int
)