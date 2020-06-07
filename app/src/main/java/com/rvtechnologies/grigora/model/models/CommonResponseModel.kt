package com.rvtechnologies.grigora.model.models

import com.google.gson.annotations.SerializedName

class CommonResponseModel<T>(
    @SerializedName("message")
    var message: String?,

    @SerializedName("voucher_code")
    var voucher_code: String?,

    @SerializedName("user_name")
    var user_name: String?,




    @SerializedName("status")
    var status: Boolean?,
    @SerializedName("data")
    var data: T?
)
