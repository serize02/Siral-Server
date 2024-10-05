package com.siral.utils

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

object Admin {

    private val dotenv: Dotenv = dotenv()

    val email: String = dotenv["ADMIN_EMAIL"]
    val password: String = dotenv["ADMIN_PASSWORD"]
}