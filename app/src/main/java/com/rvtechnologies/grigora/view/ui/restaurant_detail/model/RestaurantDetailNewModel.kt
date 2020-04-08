package com.rvtechnologies.grigora.view.ui.restaurant_detail.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class RestaurantDetailNewModel(
    val address: String,
    val busy_status: String,
    val delivery_fee: String,
    val cart_id: String,
    val all_data: List<AllData>,
    val closing_time: String,
    val cuisines: String,
    val estimated_preparing_time: String,
    val restaurant_image: String,
    val restaurant_profile_image: String,
    val featured_items: List<MealItem>,
    val french_address: String,
    val full_time: String,
    val latitude: String,
    val longitude: String,
    val no_of_seats: Int,
    val opening_time: String,
    val order_type: String,
    val pickup: String,
    val popular_items: List<MealItem>,
    val previous_ordered_items: List<MealItem>,
    val promo: List<Promo>,
    val restaurant_id: String,
    val restaurant_name: String,
    val table_booking: String,
    val total_cart_item: Int,
    val total_rating: String,
    val total_review: Int,
    val veg: String,
    val veg_item: Boolean,
    val non_veg_item: Boolean,
    val egg_item: Boolean,
    var normal_cart: NormalCart?,
    var cart: Cart?
) {
    @SuppressLint("ParcelCreator")
    @Parcelize
    data class NormalCart(
        val cart_type: String,
        val created_at: String,
        val group_order: String,
        val id: Int,
        val max_per_person: String,
        val quantity: Int,
        val restaurant_id: Int,
        val restaurant_name: String,
        val share_link: String,
        val status: String,
        val total_price: String,
        val updated_at: String,
        val user_id: String
    ) : Parcelable

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
        val user_id: String,
        @SerializedName("user_name")
        val name: String
    ) : Parcelable


    @SuppressLint("ParcelCreator")
    @Parcelize
    data class MealItem(
        val time: String,
        val total_orders: String,
        val customers: String,
        val approved: String,
        val approx_prep_time: String,
        val avg_ratings: Double,
        val created_at: String,
        val cuisine_id: Int,
        val cuisine_name: String,
        val description: String,
        val featured: String,
        val french_description: String,
        val french_name: String,
        val id: Int,
        val image: String,
        val in_offer: String,
        val item_categories: List<ItemCategory>,
        val item_count_in_cart:Int,
        val name: String,
        val offer_price: String,
        val parent_cuisine_id: Int,
        var price: String,
        val pure_veg: String,
        val restaurant_id: Int,
        val restaurant_name: String,
        val status: String,
        val total_rating: Int,
        val updated_at: String
    ) : Parcelable {
        @SuppressLint("ParcelCreator")
        @Parcelize
        data class ItemCategory(
            val created_at: String,
            val french_name: String,
            val id: Int,
            val item_id: Int,
            val item_sub_category: List<ItemSubCategory>,
            val name: String,
            val selection: String,
            val status: String,
            val optional: String,
            val updated_at: String,
            var min: String = "1",
            var max: String = "2",
            var required: String = "1"
        ) : Parcelable {
            @SuppressLint("ParcelCreator")
            @Parcelize
            data class ItemSubCategory(
                var checked: Boolean = false,
                var addOnPriceString: String,
                val add_on_price: Double,
                val created_at: String,
                val french_name: String,
                val id: Int,
                val item_cat_id: Int,
                val name: String,
                val status: String,
                val updated_at: String,
                val item_sub_sub_category: ArrayList<ItemSubSubCategory>
            ) : Parcelable
        }
    }

    @SuppressLint("ParcelCreator")
    @Parcelize
    data class ItemSubSubCategory(
        var checked: Boolean = false,
        val add_on_price: String,
        val created_at: String,
        val french_name: String,
        val id: Int,
        val item_sub_cat_id: Int,
        val max: Int,
        val min: Int,
        val name: String,
        val required: String,
        val status: String,
        val updated_at: String
    ) : Parcelable

    data class Promo(
        val code: String,
        val created_at: String,
        val description: String,
        val french_description: String,
        val french_name: String,
        val id: Int,
        val image: String,
        val min_order_value: Int,
        val name: String,
        val no_of_attempts: Int,
        val percentage: Int,
        val status: String,
        val updated_at: String
    )

    data class AllData(
        val category_name: String,
        val `data`: List<Data>
    ) {
        data class Data(
            val background_icon: String,
            val created_at: String,
            val french_name: String,
            val items_count: String,

            val icon: String,
            val id: Int,
            var filter: String,
            val image: String,
            val name: String,
            val status: String,
            val updated_at: String
        )
    }


}