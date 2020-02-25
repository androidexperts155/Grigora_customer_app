package com.rvtechnologies.grigora.model

import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.view.ui.groupCart.GroupCartType

data class GroupCartModel(
    var add_more_items: List<MenuItemModel>,
    var app_fee: Int,
    var cart_details: List<CartDetail>,
    var cart_type: String,
    var closingTime: Any,
    var created_at: String,
    var fullTime: Any,
    var group_order: String,
    var id: Int,
    var max_per_person: Int,
    var no_of_seats: Int,
    var openingTime: Any,
    var pickup: String,
    var quantity: Int,
    var restaurant_id: Int,
    var restaurant_image: String,
    var restaurant_name: String,
    var share_link: String,
    var status: String,
    var table_booking: String,
    var estimated_delivery_time: String,
    var estimated_preparing_time: String,
    var total_price: String,
    var updated_at: String,
    var user_id: Int,
    var delivery_fee: String,
    var cartTotal: String?,
    var cartSubTotal: String?,
    var beforePromo: Double?,
    var afterPromo: Double?
): GroupCartType