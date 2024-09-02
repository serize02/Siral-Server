package com.siral.request

import com.siral.utils.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ScheduleItemRequest(
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val breakfast: Boolean,
    val lunch: Boolean,
    val dinner: Boolean
)
