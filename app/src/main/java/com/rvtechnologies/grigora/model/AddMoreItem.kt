package com.rvtechnologies.grigora.model

data class AddMoreItem(
    val approx_prep_time: String,
    val avg_ratings: String,
    val created_at: String,
    val cuisine_id: Int,
    val cuisine_name: String,
    val description: String,
    val french_description: String,
    val french_name: String,
    val id: Int,
    val image: String,
    val in_offer: String,
    val item_categories: List<Any>,
    val item_count_in_cart: String,
    val name: String,
    val offer_price: Int,
    val price: Double,
    val pure_veg: String,
    val restaurant_id: Int,
    val restaurant_name: String,
    val status: String,
    val total_rating: String,
    val updated_at: String
)