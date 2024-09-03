package com.siral.data.logs

import java.time.LocalDateTime

data class Log(
    val id: Long,
    val userId: Long,
    val action: String,
    val timestamp: LocalDateTime
)
