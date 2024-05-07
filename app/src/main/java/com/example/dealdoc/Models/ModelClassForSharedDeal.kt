package com.example.dealdoc.Models

data class ModelClassForSharedDeal(
    val `data`: ShareDeal,
    val status: Boolean
)
data class ShareDeal(
    val createdAt: String,
    val createdBy: Int,
    val dealId: String,
    val description: String,
    val id: Int,
    val isDeleted: Boolean,
    val updatedAt: String,
    val userId: Int
)