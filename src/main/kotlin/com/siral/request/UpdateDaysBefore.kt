package com.siral.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateDaysBefore(
    val days: Int
)