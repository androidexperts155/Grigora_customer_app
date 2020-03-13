package com.rvtechnologies.grigora.model.models

import com.google.gson.annotations.SerializedName

class CommonResponseModel1<T>(
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("data")
    var data: T?
)
