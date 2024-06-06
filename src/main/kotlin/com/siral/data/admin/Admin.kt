package com.siral.data.admin

import java.util.*

data class Admin(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
)
