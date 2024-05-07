package com.example.dealdoc.Models

class comment_data (
    val fullName: String,
    val date : String,
    val time : String,
    val statement: String,
    val profile: String,
    val Replies: List<Reply>,
    val CommentId: Int,
    val DealId: Int
    )