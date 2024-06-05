package com.siral.data.dinninghall

import java.util.*

data class DinningHall(
    val id: String = UUID.randomUUID().toString(),
    val name: String
)
