package com.siral.data.database.tables

import org.jetbrains.exposed.sql.Table

object SiteManagerSchedulers: Table() {
    val id = long("id").autoIncrement()
    val email = varchar("email", 50)
    val dinninghallID = reference("dinninghall_id", Dinninghalls.id)
    val role = varchar("role", 50)
    override val primaryKey = PrimaryKey(id)
}