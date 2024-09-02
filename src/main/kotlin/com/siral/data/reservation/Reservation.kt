package com.siral.data.reservation

import com.siral.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Reservation(
    val id: Long,
    val studentID: Long,
    val scheduleItemID: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dateOfReservation: LocalDateTime
)
