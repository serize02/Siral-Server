package com.siral.data.models

import java.time.LocalDateTime

data class Student(
    val id: Long,
    val name: String,
    val code: Long,
    val email: String,
    val resident: Boolean,
    val last: LocalDateTime,
    val active: Boolean
)
