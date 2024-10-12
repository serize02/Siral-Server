package com.siral.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SiteManagerScheduler(
    val id: Long,
    val email: String,
    val dinninghallID: Long,
    val role: String
)
