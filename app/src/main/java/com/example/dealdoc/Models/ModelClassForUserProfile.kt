package com.example.dealdoc.Models

import android.media.Image

data class ModelClassForUserProfile(
    val `data`: DataXX,
    val message: String,
    val success: Boolean
)
data class DataXX(
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

data class GetUserDataModelRequired(
    val fullName: Any,
    val phone_no: String,
    val company: Any,
)
data class profileData(
    val `data`: Dataprofile,
    val message: String,
    val status: Boolean
)
data class Dataprofile(
    val mimetype: String,
    val name: String,
    val size: Int
)