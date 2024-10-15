package com.siral.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object AvailabilityConfigs: Table("availability_configs") {
    val dininghallId = long("dininghall_id").references(Dininghalls.id, onDelete = ReferenceOption.CASCADE)
    val daysBefore = integer("days_before").default(30)
    override val primaryKey = PrimaryKey(dininghallId)
}