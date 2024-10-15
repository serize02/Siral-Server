package com.siral.responses

import com.siral.utils.Messages
import com.siral.utils.UserRole
import kotlinx.serialization.Serializable

@Serializable
class AuthResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val message: Messages,
    val role: String?,
    val token: String?,
)