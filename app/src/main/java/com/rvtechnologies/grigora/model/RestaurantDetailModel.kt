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

    @SerializedName("table_booking")
    var table_booking: String = "",

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
    data class AllData(
        @SerializedName("items")
        var items: ArrayList<MenuItemModel> = ArrayList(),
        @SerializedName("name")
        var name: String = ""
    ) : Parcelable {
       /* @SuppressLint("ParcelCreator")
        @Parcelize
        data class Item(

            @SerializedName("restaurant_offer")
            @Expose
            var restaurant_offer: Int?,
            var offPercentage: String?,
            var nameToShow: String?,
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
            var itemCategories: List<ItemCategories> = listOf(),
            @SerializedName("name")
            var name: String = "",
            @SerializedName("offer_price")
            var offerPrice: Int = 0,
            @SerializedName("price")
            var price: String= "",

            @SerializedName("pure_veg")
            var pureVeg: String = "",

            @SerializedName("total_rating")
            @Expose
            var total_rating: Int?,

            @SerializedName("item_count_in_cart")
            @Expose
            var item_count_in_cart: Int?,

            @SerializedName("favourite")
            @Expose
            var favourite: Boolean,
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
        ) : Parcelable {
            @SuppressLint("ParcelCreator")
            @Parcelize
                data class ItemCategories(
                @SerializedName("created_at")
                var createdAt: String = "",
                @SerializedName("french_name")
                var frenchName: String = "",
                @SerializedName("id")
                var id: Int = 0,
                @SerializedName("item_id")
                var itemId: Int = 0,
                @SerializedName("item_sub_category")
                var itemSubCategory: List<ItemSubCategory> = listOf(),
                @SerializedName("name")
                var name: String = "",
                @SerializedName("selection")
                var selection: String = "",
                @SerializedName("status")
                var status: String = "",
                @SerializedName("updated_at")
                var updatedAt: String = ""
            ) : Parcelable {
                @SuppressLint("ParcelCreator")
                @Parcelize
                data class ItemSubCategory(
                    @SerializedName("add_on_price")
                    var addOnPrice: String = "",
                    @SerializedName("created_at")
                    var createdAt: String = "",
                    @SerializedName("french_name")
                    var frenchName: String = "",
                    @SerializedName("id")
                    var id: Int = 0,
                    @SerializedName("item_cat_id")
                    var itemCatId: Int = 0,
                    @SerializedName("name")
                    var name: String = "",
                    @SerializedName("status")
                    var status: String = "",
                    @SerializedName("updated_at")
                    var updatedAt: String = ""
                ) : Parcelable
            }
        }*/
    }
}