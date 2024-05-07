package com.example.dealdoc.Models

data class ModelClassForMyStuff(
    val status: Boolean,
    val user_sessions: List<UserSession>
)
data class UserSession(
    val Deal: Any,
    val createdAt: String,
    val deal_id: Any,
    val id: Int,
    val metadata: Metadata,
    val session_start_date: Any,
    val session_url: String,
    val updatedAt: String,
    val user_id: Int
)
data class Metadata(
    val created_at: String,
    val end_time: String,
    val event_guests: List<Any>,
    val event_type: String,
    val name: String,
    val start_time: String,
    val status: String,
    val updated_at: String,
    val uri: String
)