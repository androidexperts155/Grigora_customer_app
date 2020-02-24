package com.rvtechnologies.grigora.model

data class FaqModel(
    val answer: String,
    val created_at: String,
    val french_answer: String,
    val french_question: String,
    val icon: String,
    val id: Int,
    val question: String,
    val status: String,
    val updated_at: String
)