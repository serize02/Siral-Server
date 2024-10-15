package com.siral.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Logs: Table("logs"){
    val id = long("id").autoIncrement()
    val email = text("email")
    val action = text("action")
    val status = text("status")
    val timestamp = datetime("timestamp").defaultExpression(CurrentDateTime)
}