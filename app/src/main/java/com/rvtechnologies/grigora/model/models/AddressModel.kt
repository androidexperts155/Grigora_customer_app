package com.rvtechnologies.grigora.model.models


import com.google.gson.annotations.SerializedName

data class AddressModel(
    @SerializedName("address")
    var address: String = "",
    @SerializedName("complete_address")
    var completeAddress: String = "",
    @SerializedName("complete_french_address")
    var completeFrenchAddress: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("french_address")
    var frenchAddress: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("latitude")
    var latitude: String = "",
    @SerializedName("location_type_id")
    var locationTypeId: Int = 0,
    @SerializedName("longitude")
    var longitude: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",

    @SerializedName("location_type_name")
    var locationTypeName: String = "",

    @SerializedName("user_id")
    var userId: Int = 0,

    var isSelected: Boolean = false
)