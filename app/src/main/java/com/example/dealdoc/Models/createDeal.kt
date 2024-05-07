package com.example.dealdoc.Models

data class Data(
    val `data`: DataX,
    val message: String,
    val refreshToken: String,
    val success: Boolean,
    val token: String
)
data class DataX(
    val appleID: String,
    val company: Any,
    val createdAt: String,
    val email: String,
    val fullName: Any,
    val id: Int,
    val password: Any,
    val phone_no: String,
    val profilePhoto: Any,
    val resetToken: Any,
    val resetTokenExpiry: Any,
    val role_id: Int,
    val updatedAt: String
)
data class ModelCLassForCreateDeal(
    val `data`: ModelClassForDealCreate,
    val message: String,
    val success: Boolean
)
data class ModelClassForDealCreate(
    val is_draft: Boolean,
    val is_video_purchased: Boolean,
    val in_review: Boolean,
    val is_session_purchased: Boolean,
    val closed_date: String,
    val id: Int,
    val deal_name: String,
    val investment_size: Int,
    val deal_created_by: Int,
    val updatedAt: String,
    val createdAt: String
)
