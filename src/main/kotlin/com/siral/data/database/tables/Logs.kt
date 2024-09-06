package com.siral.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Logs: Table(){
    val id = long("id").autoIncrement()
    val email = varchar("email", 50)
    val action = varchar("action", 50)
    val status = varchar("status", 50)
    val timestamp = datetime("timestamp").defaultExpression(CurrentDateTime)
}