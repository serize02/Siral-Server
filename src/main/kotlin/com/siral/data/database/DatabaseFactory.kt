package com.siral.data.database

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {

    private val dotenv: Dotenv = dotenv()

    fun init(): Database {
        return Database.connect(
            url = dotenv["DB_HOST"],
            user = dotenv["DB_USER"],
            driver = "org.postgresql.Driver",
            password = dotenv["DB_PASSWORD"]
        )
    }
}