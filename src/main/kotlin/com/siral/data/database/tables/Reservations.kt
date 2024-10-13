package com.siral.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Reservations: Table() {
    val id = long("id").autoIncrement()
    val studentID = long("student_id").references(Students.id, onDelete = ReferenceOption.CASCADE)
    val scheduleItemId = long("schedule_item_id").references(Schedule.id, onDelete = ReferenceOption.CASCADE)
    val dateOfReservation = datetime("reservation_date").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}