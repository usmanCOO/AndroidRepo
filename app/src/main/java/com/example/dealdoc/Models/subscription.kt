package com.example.dealdoc.Models

data class subscription(
    val message: String,
    val subscription: SubscriptionX,
    val success: Boolean
)
data class SubscriptionX(
    val createdAt: String,
    val duration: String,
    val enddate: String,
    val id: Int,
    val startdate: String,
    val status: Boolean,
    val updatedAt: String,
    val userId: Int
)