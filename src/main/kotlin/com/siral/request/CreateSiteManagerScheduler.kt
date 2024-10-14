package com.siral.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateSiteManagerScheduler(
    val email: String,
    val role: String,
    val dininghallId: Long
)
