package com.siral.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthCredentials(
    val username: String,
    val password: String,
    val dinningHall: String
)
