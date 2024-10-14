package com.siral.data.models

import com.siral.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Student(
    val id: Long,
    val name: String,
    val code: Long,
    val email: String,
    val resident: Boolean,
    val lastReservationId: Long? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val last: LocalDateTime,
    val active: Boolean
)
