package com.siral.data.models

import com.siral.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Log(
    val id: Long,
    val email: String,
    val action: String,
    val status: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val timestamp: LocalDateTime
)
