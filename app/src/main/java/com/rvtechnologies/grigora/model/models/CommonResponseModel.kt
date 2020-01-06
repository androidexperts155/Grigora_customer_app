package com.rvtechnologies.grigora.model.models

import com.google.gson.annotations.SerializedName

class CommonResponseModel<T>(
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?,
    @SerializedName("data")
    var data: T?
)
