package com.siral.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.booleanLiteral
import org.jetbrains.exposed.sql.javatime.date

object Schedule: Table("schedules"){
    val id = long("id").autoIncrement()
    val itemDate = date("item_date")
    val time = text("time")
    val dininghallId = long("dininghall_id").references(Dininghalls.id, onDelete = ReferenceOption.CASCADE)
    val available = bool("available").defaultExpression(booleanLiteral(false))

    override val primaryKey = PrimaryKey(id)
}