package com.siral.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateReservation(
    val studentId: Long,
    val scheduleItemId: Long
)