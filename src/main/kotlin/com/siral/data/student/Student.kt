package com.siral.data.student

import java.time.LocalDate
import java.util.*

data class Student(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val dinningHall: String,
    val last: String = LocalDate.now().toString(),
    val active: Boolean = true
)
