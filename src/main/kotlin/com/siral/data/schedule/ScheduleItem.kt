package com.siral.data.schedule

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ScheduleItem(
    val id: String = UUID.randomUUID().toString(),
    val date: String, //yy-mm-dd
    val time: String, //breakfast, lunch, dinner
    val dinningHall: String,
    val active: Boolean = false
)