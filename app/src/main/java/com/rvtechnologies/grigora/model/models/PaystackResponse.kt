package com.rvtechnologies.grigora.model.models


import com.google.gson.annotations.SerializedName

data class PaystackResponse(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Boolean = false
) {
    data class Data(
        @SerializedName("amount")
        var amount: Int = 0,
        @SerializedName("authorization")
        var authorization: Authorization = Authorization(),
        @SerializedName("channel")
        var channel: String = "",
        @SerializedName("currency")
        var currency: String = "",
        @SerializedName("customer")
        var customer: Customer = Customer(),
        @SerializedName("domain")
        var domain: String = "",
        @SerializedName("fees")
        var fees: Int = 0,
        @SerializedName("gateway_response")
        var gatewayResponse: String = "",
        @SerializedName("ip_address")
        var ipAddress: String = "",
        @SerializedName("log")
        var log: Any = Any(),
        @SerializedName("message")
        var message: String = "",
        @SerializedName("metadata")
        var metadata: Metadata = Metadata(),
        @SerializedName("plan")
        var plan: Any = Any(),
        @SerializedName("reference")
        var reference: String = "",
        @SerializedName("status")
        var status: String = "",
        @SerializedName("transaction_date")
        var transactionDate: String = ""
    ) {
        data class Authorization(
            @SerializedName("authorization_code")
            var authorizationCode: String = "",
            @SerializedName("bank")
            var bank: String = "",
            @SerializedName("bin")
            var bin: String = "",
            @SerializedName("brand")
            var brand: String = "",
            @SerializedName("card_type")
            var cardType: String = "",
            @SerializedName("channel")
            var channel: String = "",
            @SerializedName("country_code")
            var countryCode: String = "",
            @SerializedName("exp_month")
            var expMonth: String = "",
            @SerializedName("exp_year")
            var expYear: String = "",
            @SerializedName("last4")
            var last4: String = "",
            @SerializedName("reusable")
            var reusable: Boolean = false,
            @SerializedName("signature")
            var signature: String = ""
        )

        data class Customer(
            @SerializedName("customer_code")
            var customerCode: String = "",
            @SerializedName("email")
            var email: String = "",
            @SerializedName("first_name")
            var firstName: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("last_name")
            var lastName: String = "",
            @SerializedName("metadata")
            var metadata: Metadata = Metadata(),
            @SerializedName("phone")
            var phone: String = "",
            @SerializedName("risk_action")
            var riskAction: String = ""
        ) {
            class Metadata
        }

        data class Metadata(
            @SerializedName("custom_fields")
            var customFields: List<CustomField> = listOf()
        ) {
            data class CustomField(
                @SerializedName("display_name")
                var displayName: String = "",
                @SerializedName("value")
                var value: String = "",
                @SerializedName("variable_name")
                var variableName: String = ""
            )
        }
    }
}