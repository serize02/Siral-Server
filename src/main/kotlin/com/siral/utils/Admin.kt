package com.siral.utils

import com.siral.request.AuthCredentials
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

object Admin {
    private val dotenv: Dotenv = dotenv()

    private val email: String = dotenv["ADMIN_EMAIL"]
    private val password: String = dotenv["ADMIN_PASSWORD"]

    fun verify(credentials: AuthCredentials): Boolean {
        if(credentials.email != email)
            return false
        if(credentials.password != password)
            return false
        return true
    }

}