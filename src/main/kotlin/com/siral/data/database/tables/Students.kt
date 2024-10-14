package com.siral.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.booleanLiteral
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Students: Table("students") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 64)
    val code = long("student_code")
    val email = varchar("email", 32).uniqueIndex()
    val resident = bool("resident")
    val lastReservationId = long("last_reservation_id").references(Reservations.id).nullable()
    val last = datetime("last_action").defaultExpression(CurrentDateTime)
    val active = bool("active").defaultExpression(booleanLiteral(true))

    override val primaryKey = PrimaryKey(id)
}