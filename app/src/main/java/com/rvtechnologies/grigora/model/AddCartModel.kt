package com.rvtechnologies.grigora.model


import com.google.gson.annotations.SerializedName

data class AddCartModel(
    @SerializedName("app_fee")
    var appFee: Int = 0,
    @SerializedName("cart_details")
    var cartDetails: List<CartDetail> = listOf(),
    @SerializedName("cart_id")
    var cartId: Int = 0,
    @SerializedName("cart_type")
    var cartType: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("delivery_fee")
    var deliveryFee: String = "",
    @SerializedName("group_order")
    var groupOrder: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("max_per_person")
    var maxPerPerson: Any = Any(),
    @SerializedName("quantity")
    var quantity: Int = 0,
    @SerializedName("restaurant_id")
    var restaurantId: Int = 0,
    @SerializedName("restaurant_image")
    var restaurantImage: String = "",
    @SerializedName("restaurant_name")
    var restaurantName: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("total_price")
    var totalPrice: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("user_id")
    var userId: Int = 0
) {
    data class CartDetail(
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("item_choices")
        var itemChoices: Any = Any(),
        @SerializedName("item_french_name")
        var itemFrenchName: String = "",
        @SerializedName("item_id")
        var itemId: Int = 0,
        @SerializedName("item_name")
        var itemName: String = "",
        @SerializedName("price")
        var price: String = "",
        @SerializedName("quantity")
        var quantity: Int = 0,
        @SerializedName("updated_at")
        var updatedAt: String = "",
        @SerializedName("user_id")
        var userId: Int = 0
    )
}