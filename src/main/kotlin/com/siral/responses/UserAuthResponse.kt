package com.siral.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthResponse(
    val id: String,
    val token: String,
)
