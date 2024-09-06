package com.siral.data.database

import com.siral.data.database.tables.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseInitializer {
    fun init(db: Database) {
        transaction(db) {
            SchemaUtils.run {
                createMissingTablesAndColumns(
                    Students, Dinninghalls, Schedule, Reservations, SiteManagerSchedulers, Logs
                )
            }
        }
    }
}