package com.rvtechnologies.grigora.model


import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.rvtechnologies.grigora.model.models.MenuItemModel
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class RestaurantDetailModel(
    @SerializedName("all_data")
    var allData: ArrayList<AllData> = ArrayList(),
    @SerializedName("popluar_items")
    var popluarItems: ArrayList<MenuItemModel> = ArrayList(),
    @SerializedName("previous_ordered_items")
    var previousOrderedItems: ArrayList<MenuItemModel> = ArrayList(),

    @SerializedName("cart")
    var cart: Cart?,

    @SerializedName("table_booking")
    var table_booking: String = "",


    @SerializedName("cart_id")
    var cart_id: String = "",

    @SerializedName("no_of_seats")
    var no_of_seats: String = "",

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

) : Parcelable {

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class Cart(
        @SerializedName("cart_type")
        val cart_type: String,
        @SerializedName("created_at")
        val created_at: String,
        @SerializedName("group_order")
        val group_order: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("max_per_person")
        val max_per_person: Int,
        @SerializedName("quantity")
        val quantity: Int,
        @SerializedName("restaurant_id")
        val restaurant_id: Int,
        @SerializedName("share_link")
        val share_link: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("total_price")
        val total_price: String,
        @SerializedName("updated_at")
        val updated_at: String,
        @SerializedName("user_id")
        val user_id: Int,
        @SerializedName("user_name")
        val name: String
    ) : Parcelable


    @SuppressLint("ParcelCreator")
    @Parcelize
    data class AllData(
        @SerializedName("items")
        var items: ArrayList<MenuItemModel> = ArrayList(),
        @SerializedName("name")
        var name: String = ""
    ) : Parcelable
}