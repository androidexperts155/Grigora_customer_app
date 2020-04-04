package com.rvtechnologies.grigora.view.ui.restaurant_detail.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class RestaurantDetailNewModel(
    val address: String,
    val busy_status: String,
    val cart_id: String,
    val all_data: List<AllData>,
    val closing_time: String,
    val cuisines: String,
    val estimated_preparing_time: String,
    val restaurant_image: String,
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
    var normal_cart: NormalCart?
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
    data class MealItem(
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
        val name: String,
        val offer_price: Int,
        val parent_cuisine_id: Int,
        val price: String,
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
            val updated_at: String
        ) : Parcelable {
            @SuppressLint("ParcelCreator")
            @Parcelize
            data class ItemSubCategory(
                var checked: Boolean,
                var addOnPriceString: String,
                val add_on_price: String,
                val created_at: String,
                val french_name: String,
                val id: Int,
                val item_cat_id: Int,
                val name: String,
                val status: String,
                val updated_at: String
            ) : Parcelable
        }
    }

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
            val icon: String,
            val id: Int,
            val image: String,
            val name: String,
            val status: String,
            val updated_at: String
        )
    }


}