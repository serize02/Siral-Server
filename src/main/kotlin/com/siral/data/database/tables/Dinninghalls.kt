package com.siral.data.database.tables

import org.jetbrains.exposed.sql.Table

object Dinninghalls: Table(){
    val id = long("id").autoIncrement()
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id)
}