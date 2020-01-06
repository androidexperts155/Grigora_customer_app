package com.rvtechnologies.grigora.model


import com.google.gson.annotations.SerializedName

data class AddMoneyModel(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Boolean = false,
    @SerializedName("wallet")
    var wallet: Int = 0
)