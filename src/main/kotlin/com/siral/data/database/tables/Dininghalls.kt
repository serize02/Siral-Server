package com.siral.data.database.tables

import org.jetbrains.exposed.sql.Table

object Dininghalls: Table("dininghalls"){
    val id = long("id").autoIncrement()
    val name = text("name").uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}