package com.siral.data

import com.siral.utils.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Data(
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val dinningHall: String,
    val reservations: Long
)