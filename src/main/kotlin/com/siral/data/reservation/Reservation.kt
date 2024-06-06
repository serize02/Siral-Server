package com.siral.data.reservation

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.*

@Serializable
data class Reservation(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val scheduleItemId: String,
    val dateOfReservation: String = LocalDate.now().toString()
)
