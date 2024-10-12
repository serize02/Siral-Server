package com.siral.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DinningHall(
    val id: Long,
    val name: String
)
