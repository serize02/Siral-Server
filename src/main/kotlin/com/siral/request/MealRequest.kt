package com.siral.request

import kotlinx.serialization.Serializable

@Serializable
data class MealRequest(
    val date: String, //yy-mm-dd
    val time: String, //breakfast, lunch, dinner
    val dinningHall: String
)
