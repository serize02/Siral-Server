package com.siral.responses

import com.siral.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class StudentLoginData(
    val id: Long,
    val token: String,
    val name: String,
    val code: Long,
    val email: String,
    val resident: Boolean,
    @Serializable(with = LocalDateTimeSerializer::class)
    val last: LocalDateTime,
    val active: Boolean
)