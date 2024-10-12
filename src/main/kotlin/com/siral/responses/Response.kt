package com.siral.responses

import kotlinx.serialization.Serializable

@Serializable
open class Response<T>(
    val success: Boolean = false,
    val data: T? = null,
    val message: String,
    val status: Int
)
