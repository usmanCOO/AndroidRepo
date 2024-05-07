package com.example.dealdoc.Models

data class commentdata(
    val `data`: List<Datacomment>,
    val message: String,
    val success: Boolean
)
data class Datacomment(
    val Replies: List<Reply>,
    val User: UserXX,
    val createdAt: String,
    val created_by: Int,
    val deal_id: Int,
    val id: Int,
    val replied_to: Any,
    val statement: String,
    val updatedAt: String
)
data class UserXX(
    val appleID: String,
    val company: String,
    val createdAt: String,
    val email: String,
    val fullName: String,
    val id: Int,
    val password: Any,
    val phone_no: String,
    val profilePhoto: String,
    val resetToken: Any,
    val resetTokenExpiry: Any,
    val role_id: Int,
    val updatedAt: String
)
data class Reply(
    val User: UserXX,
    val createdAt: String,
    val created_by: Int,
    val deal_id: Int,
    val id: Int,
    val replied_to: Int,
    val statement: String,
    val updatedAt: String
)