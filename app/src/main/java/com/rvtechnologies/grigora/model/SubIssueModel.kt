package com.rvtechnologies.grigora.model

data class SubIssueModel(
    val created_at: String,
    val french_name: String,
    val id: Int,
    val issue_id: Int,
    val name: String,
    val status: String,
    val updated_at: String
)