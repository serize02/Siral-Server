package com.siral.data.user

import java.time.LocalDate
import java.util.*

data class User(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val role: String,
    val dinningHall: String,
    val last: String = LocalDate.now().toString(),
    val active: Boolean = true
)
