package com.siral.responses

import kotlinx.serialization.Serializable

@Serializable
class AuthResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String,
    val status: Int,
    val role: String?,
    val token: String?,
)