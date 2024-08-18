package com.siral.data.student

import java.time.LocalDateTime

data class StudentInfo(
    val id: Long,
    val name: String,
    val code: Long,
    val email: String,
    val resident: Boolean,
    val last: LocalDateTime,
    val active: Boolean
)
