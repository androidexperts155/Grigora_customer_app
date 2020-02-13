package com.rvtechnologies.grigora.model


import com.google.gson.annotations.SerializedName

data class QuizModel(
    @SerializedName("answer")
    var answer: String = "",
    @SerializedName("coupon_code")
    var couponCode: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image")
    var image: String = "",
    @SerializedName("join_quiz")
    var joinQuiz: String = "",
    @SerializedName("no_of_winners")
    var noOfWinners: Int = 0,
    @SerializedName("offer_expiry")
    var offerExpiry: String = "",
    @SerializedName("offer_points")
    var offerPoints: Int = 0,
    @SerializedName("option1")
    var option1: Any = Any(),
    @SerializedName("option2")
    var option2: Any = Any(),
    @SerializedName("option3")
    var option3: Any = Any(),
    @SerializedName("option4")
    var option4: Any = Any(),
    @SerializedName("options")
    var options: List<String> = ArrayList(),
    @SerializedName("question")
    var question: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("type")
    var type: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    var selected:Int=-1
)