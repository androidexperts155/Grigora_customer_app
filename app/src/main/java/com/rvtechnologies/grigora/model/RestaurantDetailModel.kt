package com.rvtechnologies.grigora.model


import com.google.gson.annotations.SerializedName

data class RestaurantDetailModel(
    @SerializedName("all_data")
    var allData: List<AllData> = listOf(),
    @SerializedName("popluar_items")
    var popluarItems: ArrayList<AllData.Item> = ArrayList(),
    @SerializedName("previous_ordered_items")
    var previousOrderedItems: ArrayList<AllData.Item> = ArrayList(),

    @SerializedName("restaurant_name")
    var restaurant_name: String = "",


    @SerializedName("restaurant_id")
    var restaurant_id: String = "",

    @SerializedName("full_time")
    var full_time: String = "",

    @SerializedName("estimated_preparing_time")
    var estimated_preparing_time: String = "",



    @SerializedName("cuisines")
    var cuisines: String = "",

    @SerializedName("total_rating")
    var total_rating: String = "",

    @SerializedName("total_review")
    var total_review: String = "",

    @SerializedName("veg")
    var veg: String = "",

    @SerializedName("pickup")
    var pickup: String = "",

    @SerializedName("opening_time")
    var opening_time: String = "",

    @SerializedName("closing_time")
    var closing_time: String = ""

) {
    data class AllData(
        @SerializedName("items")
        var items: ArrayList<Item> = ArrayList(),
        @SerializedName("name")
        var name: String = ""
    ) {
        data class Item(
            var index: Int,
            @SerializedName("item_count_in_cart")
            var cart_quantity: String = "",
            @SerializedName("approx_prep_time")
            var approxPrepTime: String = "",
            @SerializedName("avg_ratings")
            var avgRatings: String = "",
            @SerializedName("created_at")
            var createdAt: String = "",
            @SerializedName("cuisine_id")
            var cuisineId: Int = 0,
            @SerializedName("cuisine_name")
            var cuisineName: String = "",
            @SerializedName("description")
            var description: String = "",
            @SerializedName("french_description")
            var frenchDescription: String = "",
            @SerializedName("french_name")
            var frenchName: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("image")
            var image: String = "",
            @SerializedName("in_offer")
            var inOffer: String = "",
            @SerializedName("item_categories")
            var itemCategories: List<Any> = listOf(),
            @SerializedName("name")
            var name: String = "",
            @SerializedName("offer_price")
            var offerPrice: Int = 0,
            @SerializedName("price")
            var price: Int = 0,
            @SerializedName("pure_veg")
            var pureVeg: String = "",
            @SerializedName("restaurant_id")
            var restaurantId: Int = 0,
            @SerializedName("restaurant_name")
            var restaurantName: String = "",
            @SerializedName("status")
            var status: String = "",
            @SerializedName("total_rating")
            var totalRating: String = "",
            @SerializedName("updated_at")
            var updatedAt: String = ""
        )
    }


}