package com.example.dealdoc.Models

data class unreadMessages(
    val message: String,
    val success: Boolean,
    val updatereadstatus: List<Int>
)