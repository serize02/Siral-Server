package com.siral.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object SiteManagerSchedulers: Table() {
    val id = long("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val dinninghallID = long("dinninghall_id").references(Dinninghalls.id, onDelete = ReferenceOption.CASCADE)
    val role = varchar("role", 255)
    override val primaryKey = PrimaryKey(id)
}