package com.siral.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthResponse(
    val token: String,
    val id: String,
    val role: String
)
