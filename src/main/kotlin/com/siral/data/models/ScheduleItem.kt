package com.siral.data.models

import com.siral.utils.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ScheduleItem(
    val id: Long,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate, //yy-mm-dd
    val time: String, //breakfast, lunch, dinner
    val dinninghallId: Long,
    val available: Boolean
)