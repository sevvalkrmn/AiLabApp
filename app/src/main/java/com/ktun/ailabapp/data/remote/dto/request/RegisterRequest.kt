package com.ktun.ailabapp.data.remote.dto.request

data class RegisterRequest(
    val fullName: String,
    val username: String,
    val email: String,
    val schoolNumber: String,
    val phoneNumber: String,
    val password: String
)