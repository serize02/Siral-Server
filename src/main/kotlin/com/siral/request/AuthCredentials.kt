package com.siral.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthCredentials(
    val email: String,
    val password: String,
)
