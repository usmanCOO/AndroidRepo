package com.example.dealdoc.Models

data class sent_comment(
    val bulkobjs: List<Bulkobj>,
    val `data`: Data_comment,
    val dealExist: List<DealExist>,
    val message: String,
    val success: Boolean
)
data class DealExist(
    val createdBy: Int,
    val userId: Int
)
data class Data_comment(
    val createdAt: String,
    val created_by: Int,
    val deal_id: String,
    val id: Int,
    val statement: String,
    val updatedAt: String
)
data class Bulkobj(
    val comment_Id: Int,
    val deal_Id: String,
    val message: String,
    val notification_type: String,
    val read_status: Boolean,
    val send_to: Int
)