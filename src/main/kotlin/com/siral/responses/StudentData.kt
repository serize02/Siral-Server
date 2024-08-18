package com.siral.responses

import java.time.LocalDateTime

data class StudentData(
    val name: String,
    val code: Long,
    val email: String,
    val resident: Boolean
)
