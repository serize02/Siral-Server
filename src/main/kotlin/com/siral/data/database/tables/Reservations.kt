package com.siral.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Reservations: Table() {
    val id = long("id").autoIncrement()
    val studentID = reference("student_id", Students.id)
    val scheduleItemId = reference("schedule_item_id", Schedule.id)
    val dateOfReservation = datetime("reservation_date").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}