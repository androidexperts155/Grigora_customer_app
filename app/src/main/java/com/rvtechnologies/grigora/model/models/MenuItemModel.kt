package com.rvtechnologies.grigora.model.models


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class MenuItemModel(

    var cartId: String = "",
    var isForGroupCart: Boolean = false,

    @SerializedName("id")
    @Expose
    var id: Int?,


    @SerializedName("total_rating")
    @Expose
    var total_rating: Int?,

    @SerializedName("item_count_in_cart")
    @Expose
    var item_count_in_cart: Int,


    @SerializedName("restaurant_id")
    @Expose
    var restaurantId: Int?,


    @SerializedName("category_id")
    @Expose
    var categoryId: Int?,


    @SerializedName("name")
    @Expose
    var name: String?,


    @SerializedName("favourite")
    @Expose
    var favourite: Boolean,


    @SerializedName("french_name")
    @Expose
    var frenchName: String?,
    @SerializedName("description")
    @Expose
    var description: String?,
    @SerializedName("french_description")
    @Expose
    var frenchDescription: String?,
    @SerializedName("image")
    @Expose
    var image: String?,
    @SerializedName("price")
    @Expose
    var price: String?,
    @SerializedName("offer_price")
    @Expose
    var offerPrice: String?,
    @SerializedName("in_offer")
    @Expose
    var inOffer: Int?,
    @SerializedName("restaurant_offer")
    @Expose
    var restaurant_offer: Int?,
    var offPercentage: String?,
    @SerializedName("pure_veg")
    @Expose
    var pureVeg: String?,
    @SerializedName("status")
    @Expose
    var status: String?,
    @SerializedName("created_at")
    @Expose
    var createdAt: String?,
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String?,
    @SerializedName("category_name")
    @Expose
    var categoryName: String?,
    @SerializedName("item_categories")
    @Expose
    var itemCategories: List<ItemCategory>?,

    @SerializedName("item_cart")
    @Expose
    var itemCart: List<ItemCart>?,


    @SerializedName("customers")
    @Expose
    var customers: String,

    @SerializedName("total_orders")
    @Expose
    var total_orders: String,

    @SerializedName("time")
    @Expose
    var time: String,


    @SerializedName("cuisine_name")
    var cuisineName: String = "",
    var nameToShow: String?,
    var index: Int,


    @SerializedName("approx_prep_time")
    var approxPrepTime: String = "",
    @SerializedName("avg_ratings")
    var avgRatings: Float = 0F,

    @SerializedName("cuisine_id")
    var cuisineId: Int = 0,
    @SerializedName("restaurant_name")
    var restaurantName: String = ""


) : Parcelable {
    @SuppressLint("ParcelCreator")
    @Parcelize
    data class ItemCart(
        val cart_id: Int,
        val created_at: String,
        val id: Int,

        val item_id: Int,
        val price: String,
        val quantity: Int,
        val updated_at: String,
        val user_id: String
    ) : Parcelable
}