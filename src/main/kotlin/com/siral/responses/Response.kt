package com.siral.responses

import com.siral.utils.Messages
import kotlinx.serialization.Serializable

@Serializable
open class Response<T>(
    val success: Boolean = true,
    val data: T? = null,
    val message: Messages,
)
