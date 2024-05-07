package com.example.dealdoc.Models

data class ModelClassForSharedByMe(
    val `data`: List<SharedData>,
    val success: Boolean
)
data class SharedUser(
    val email: String,
    val fullName: String,
    val profilePhoto: String,
    val id: Int
)
data class Creator(
    val email: String,
    val fullName: String,
    val profilePhoto: String,
    val id: Int
)
data class SharedData(
    val Deal: SharedDeal,
    val createdAt: String,
    val createdBy: Int,
    val creator: Creator,
    val dealId: Int,
    val description: String,
    val id: Int,
    val isDeleted: Boolean,
    val shared_user: SharedUser,
    val status: Any,
    val unread: Int,
    val updatedAt: String,
    val userId: Int
)
data class SharedDeal(
    val closed_date: String,
    val color: String,
    val deal_name: String,
    val id: Int,
    val investment_size: Int,
    val updatedAt: String
)