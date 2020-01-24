package com.rvtechnologies.grigora.model.models


import com.google.gson.annotations.SerializedName

data class temp(
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("quantity")
    var quantity: Int = 0,
    @SerializedName("restaurant_id")
    var restaurantId: Int = 0,
    @SerializedName("restaurant_name")
    var restaurantName: String = "",
    @SerializedName("total_price")
    var totalPrice: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("user_id")
    var userId: Int = 0
)