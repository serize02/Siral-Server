package com.siral.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateDiningHall(
    val name: String
)