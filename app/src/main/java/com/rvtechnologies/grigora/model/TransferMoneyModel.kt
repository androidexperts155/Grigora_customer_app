package com.rvtechnologies.grigora.model


import com.google.gson.annotations.SerializedName

data class TransferMoneyModel(
    @SerializedName("wallet")
    var wallet: Int = 0
)