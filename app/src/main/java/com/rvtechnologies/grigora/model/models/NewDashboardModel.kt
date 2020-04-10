package com.rvtechnologies.grigora.model.models


import com.google.gson.annotations.SerializedName
import com.rvtechnologies.grigora.view.ui.dashboard.DashboardItemType
import com.rvtechnologies.grigora.view.ui.search.SearchItem

data class NewDashboardModel(
    @SerializedName("cuisines")
    var cuisines: ArrayList<Cuisine> = ArrayList(),
    @SerializedName("customized_data")
    var customizedData: ArrayList<CustomizedData> = ArrayList(),

    @SerializedName("all_restaurants")
    var allRestaurants: ArrayList<AllRestautants> = ArrayList(),

    @SerializedName("base_delivery_fee")
    var base_delivery_fee: String = "",

    @SerializedName("notifications")
    var notifications: Boolean = false,

    @SerializedName("driver_status")
    var driverStatus: Boolean = false,

    @SerializedName("min_kilo_meter")
    var min_kilo_meter: String = "",
    @SerializedName("is_cart")
    var is_cart: IsCart?,

    @SerializedName("filters")
    var filters: ArrayList<Filter> = ArrayList(),
    @SerializedName("promos")
    var promos: ArrayList<Promo> = ArrayList()
    ,
    @SerializedName("wallet")
    var wallet: String = ""
) : DashboardItemType {
    data class Cuisine(
        @SerializedName("background_icon")
        var background_icon: String = "",
        @SerializedName("icon")
        var icon: String = "",
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("french_name")
        var frenchName: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image")
        var image: String = "",
        @SerializedName("name")
        var name: String = "",
        @SerializedName("selected")
        var selected: Boolean = false,
        @SerializedName("status")
        var status: String = "",
        @SerializedName("updated_at")
        var updatedAt: String = ""
    ) : DashboardItemType

    data class CustomizedData(
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("preparing_time")
        var preparing_time: String = "",

        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("restaurants")
        var restaurants: ArrayList<Restaurant> = ArrayList(),
        @SerializedName("show_all")
        var showAll: String = "",
        @SerializedName("status")
        var status: String = "",
        @SerializedName("title")
        var title: String = "",
        @SerializedName("ui_type")
        var uiType: String = "",
        @SerializedName("updated_at")
        var updatedAt: String = "",
        @SerializedName("background_image")
        var backgroundImage: String = "",
        @SerializedName("french_name")
        var frenchName: String = "",
        @SerializedName("image")
        var image: String = "",
        @SerializedName("name")
        var name: String = "",
        @SerializedName("selected")
        var selected: Boolean = false

    ) : DashboardItemType {
        data class Restaurant(
            @SerializedName("table_booking")
            var table_booking: String = "",

            @SerializedName("items")
            var items: ArrayList<Image> = ArrayList(),


            @SerializedName("no_of_seats")
            var no_of_seats: String = "",
            var uiTpe: String,
            @SerializedName("preparing_time")
            var preparing_time: String = "",
            @SerializedName("address")
            var address: String = "",
            @SerializedName("approved")
            var approved: String = "",
            @SerializedName("average_rating")
            var averageRating: Int = 0,
            @SerializedName("busy_status")
            var busyStatus: String = "",
            @SerializedName("closing_time")
            var closingTime: String ="",
            @SerializedName("created_at")
            var createdAt: String = "",
            @SerializedName("device_token")
            var deviceToken: Any = Any(),
            @SerializedName("device_type")
            var deviceType: Any = Any(),
            @SerializedName("email")
            var email: String = "",
            @SerializedName("email_verified_at")
            var emailVerifiedAt: Any = Any(),
            @SerializedName("facebook_id")
            var facebookId: Any = Any(),
            @SerializedName("french_address")
            var frenchAddress: String = "",
            @SerializedName("french_name")
            var frenchName: String = "",
            @SerializedName("full_time")
            var fullTime: String = "",
            @SerializedName("google_id")
            var googleId: Any = Any(),
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("id_proof")
            var idProof: String = "",
            @SerializedName("image")
            var image: String = "",
            @SerializedName("instagram_id")
            var instagramId: Any = Any(),
            @SerializedName("language")
            var language: String = "",
            @SerializedName("latitude")
            var latitude: String = "",
            @SerializedName("longitude")
            var longitude: String = "",
            @SerializedName("name")
            var name: String = "",
            @SerializedName("notification")
            var notification: String = "",
            @SerializedName("offer")
            var offer: Int = 0,
            @SerializedName("opening_time")
            var openingTime: String ="",
            @SerializedName("otp")
            var otp: Any = Any(),
            @SerializedName("otp_expire_time")
            var otpExpireTime: Any = Any(),
            @SerializedName("phone")
            var phone: String = "",
            @SerializedName("pickup")
            var pickup: String = "",
            @SerializedName("promo_id")
            var promoId: Any = Any(),
            @SerializedName("pure_veg")
            var pureVeg: String = "",
            @SerializedName("receipt_id")
            var receiptId: String = "",
            @SerializedName("role")
            var role: String = "",
            @SerializedName("status")
            var status: String = "",
            @SerializedName("total_rating")
            var totalRating: Int = 0,
            @SerializedName("twiter_id")
            var twiterId: Any = Any(),
            @SerializedName("updated_at")
            var updatedAt: String = "",
            @SerializedName("username")
            var username: String = "",
            @SerializedName("wallet")
            var wallet: String = ""
        ) : DashboardItemType
    }

    data class Filter(
        var arrowClicked: Boolean,
        @SerializedName("created_at")
        var createdAt: Any = Any(),

        @SerializedName("multi_selected")
        var multiSelected: String = "",


        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("name")
        var name: String = "",
        @SerializedName("selected")
        var selected: Boolean = false,
        @SerializedName("selection_type")
        var selectionType: String = "",
        @SerializedName("status")
        var status: String = "",
        @SerializedName("updated_at")
        var updatedAt: Any = Any()
    ) : DashboardItemType

    data class Promo(
        @SerializedName("code")
        var code: String = "",
        @SerializedName("created_at")
        var createdAt: String = "",
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
        @SerializedName("min_order_value")
        var minOrderValue: Int = 0,
        @SerializedName("name")
        var name: String = "",
        @SerializedName("no_of_attempts")
        var noOfAttempts: Int = 0,
        @SerializedName("percentage")
        var percentage: Int = 0,
        @SerializedName("status")
        var status: String = "",
        @SerializedName("updated_at")
        var updatedAt: String = ""
    ) : DashboardItemType

    data class AllRestautants(
        var reviewToShow:String,
        var closeToShow:String,
        var distance:String,
        var markerId:String,
        @SerializedName("address")
        var address: String = "",

        @SerializedName("reviews")
        var reviews: Int = 0,

        @SerializedName("preparing_time")
        var preparing_time: String = "",

        @SerializedName("table_booking")
        var table_booking: String = "",

        @SerializedName("no_of_seats")
        var no_of_seats: String = "",


        @SerializedName("average_rating")
        var averageRating: Float = 0F,
        @SerializedName("busy_status")
        var busyStatus: String = "",
        @SerializedName("closing_time")
        var closingTime: String = "",
        @SerializedName("french_address")
        var frenchAddress: String = "",
        @SerializedName("french_name")
        var frenchName: String = "",
        @SerializedName("full_time")
        var fullTime: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image")
        var image: String = "",
        @SerializedName("items")
        var items: ArrayList<Image> = ArrayList(),
        @SerializedName("latitude")
        var latitude: String = "",
        @SerializedName("longitude")
        var longitude: String = "",
        @SerializedName("name")
        var name: String = "",
        @SerializedName("offer")
        var offer: Int = 0,
        @SerializedName("opening_time")
        var openingTime: String = "",
        @SerializedName("pickup")
        var pickup: String = "",
        @SerializedName("promo_id")
        var promoId: Int = 0,
        @SerializedName("pure_veg")
        var pureVeg: String = "",
        @SerializedName("status")
        var status: String = "",
        @SerializedName("total_rating")
        var totalRating: String = ""
    ) : DashboardItemType, SearchItem

    data class Image(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image")
        var image: String = ""
    )

    data class IsCart(
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
        var userId: String = ""
    )
}