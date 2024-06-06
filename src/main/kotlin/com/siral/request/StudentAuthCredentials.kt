package com.siral.request

import kotlinx.serialization.Serializable

@Serializable
data class StudentAuthCredentials(
    val username: String,
    val password: String,
    val dinningHall: String
)
