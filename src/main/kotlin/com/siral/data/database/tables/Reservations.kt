package com.siral.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Reservations: Table("reservations") {
    val id = long("id").autoIncrement()
    val studentID = long("student_id").references(Students.id, onDelete = ReferenceOption.CASCADE)
    val scheduleItemId = long("schedule_id").references(Schedule.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}