package com.siral.request

import com.siral.utils.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class AvailableMeals (
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val dininghallId: Long
)