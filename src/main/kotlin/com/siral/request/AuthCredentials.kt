package com.siral.request

import kotlinx.serialization.Serializable

@Serializable
data class StudentAuthCredentials(
    val email: String,
    val password: String,
)
