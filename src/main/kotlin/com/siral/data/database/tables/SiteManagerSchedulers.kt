package com.siral.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object SiteManagerSchedulers: Table("adminstrations") {
    val id = long("id").autoIncrement()
    val email = text("email").uniqueIndex()
    val dininghallID = long("dininghall_id").references(Dininghalls.id, onDelete = ReferenceOption.CASCADE)
    val role = text("role")
    override val primaryKey = PrimaryKey(id)
}