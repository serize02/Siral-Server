package com.siral.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.booleanLiteral
import org.jetbrains.exposed.sql.javatime.date

object Schedule: Table(){
    val id = long("id").autoIncrement()
    val itemDate = date("item_date")
    val time = varchar("time", 50)
    val dinninghallId = reference("dinning_hall_id", Dinninghalls.id)
    val available = bool("available").defaultExpression(booleanLiteral(false))

    override val primaryKey = PrimaryKey(id)
}