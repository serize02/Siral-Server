package com.siral.request

import kotlinx.serialization.Serializable

@Serializable
data class NewRoleCredentials(
    val email: String,
    val role: String,
    val dinninghall: String
)
