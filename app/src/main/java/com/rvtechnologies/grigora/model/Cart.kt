package com.rvtechnologies.grigora.model

import com.rvtechnologies.grigora.view.ui.groupCart.GroupCartType

data class Cart(
    val approx_prep_time: String,
    val cart_id: Int,
    val created_at: String,
    val id: Int,
    val item_choices: List<ItemChoice>,
    val item_french_name: String,
    val item_id: Int,
    val item_name: String,
    val price: String,
    val quantity: Int,
    val updated_at: String,
    val user_id: Int,
    val veg: String
): GroupCartType