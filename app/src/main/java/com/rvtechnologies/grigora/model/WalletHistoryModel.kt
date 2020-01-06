package com.rvtechnologies.grigora.model


import com.google.gson.annotations.SerializedName

data class WalletHistoryModel(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Boolean = false,
    @SerializedName("wallet")
    var wallet: String = ""
) {
    data class Data(
        @SerializedName("amount")
        var amount: String = "",
        @SerializedName("created_at")

        var createdAt: String = "",
        @SerializedName("id")
        var id: Int = 0,

        @SerializedName("order_id")
        var orderId: Any = Any(),
        @SerializedName("other_user_image")
        var otherUserImage: String = "",

        @SerializedName("other_user_email")
        var otherUserEmail: String = "",


        @SerializedName("other_user_name")
        var otherUserName: String = "",
        @SerializedName("reason")
        var reason: String = "",
        @SerializedName("reference")
        var reference: Any = Any(),
        @SerializedName("transaction_data")
        var transactionData: String = "",
        @SerializedName("type")
        var type: String = "",
        @SerializedName("updated_at")
        var updatedAt: String = "",
        @SerializedName("user_email")
        var userEmail: String = "",
        @SerializedName("user_id")
        var userId: Int = 0,
        @SerializedName("user_image")
        var userImage: String = "",
        @SerializedName("user_name")
        var userName: String = ""
    )
}