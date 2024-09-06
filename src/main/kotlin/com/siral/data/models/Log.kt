package com.siral.data.models

import java.time.LocalDateTime

data class Log(
    val id: Long,
    val email: String,
    val action: String,
    val status: String,
    val timestamp: LocalDateTime
)
