package com.siral.data.database

import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init(): Database {
        return Database.connect(
            url = "jdbc:postgresql://localhost:5432/siraldb",
            user = System.getenv("dbuser"),
            driver = "org.postgresql.Driver",
            password = System.getenv("dbpassword")
        )
    }
}