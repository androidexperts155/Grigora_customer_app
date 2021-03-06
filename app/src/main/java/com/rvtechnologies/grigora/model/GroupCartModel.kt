package com.rvtechnologies.grigora.model

import com.rvtechnologies.grigora.model.models.MenuItemModel
import com.rvtechnologies.grigora.view.ui.groupCart.GroupCartType
import com.rvtechnologies.grigora.view.ui.restaurant_detail.model.RestaurantDetailNewModel

data class GroupCartModel(
    var add_more_items: ArrayList<RestaurantDetailNewModel.MealItem>,
    var app_fee: Int,
    var cart_details: List<CartDetail>,
    var cart_type: String,
    var discount: String = "",
    var restaurant_latitude: String,
    var restaurant_longitude: String,
    var closingTime: String,
    var created_at: String,
    var fullTime: Any,
    var busy_status: Any,
    var group_order: String,
    var id: Int,
    var max_per_person: Int,
    var no_of_seats: Int,
    var openingTime: String,
    var pickup: String,
    var delivery_fee: String,
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

    var totalPrice: String?,
    var cartTotal: String?,
    var cartSubTotal: String?,
    var beforePromo: Double?,
    var afterPromo: Double?
) : GroupCartType