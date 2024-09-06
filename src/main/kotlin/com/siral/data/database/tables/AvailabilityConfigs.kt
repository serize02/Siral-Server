package com.siral.data.database.tables

import org.jetbrains.exposed.sql.Table


object AvailabilityConfigs: Table("availability_configs") {
    val dinninghallId = reference("dinninghall_id", Dinninghalls.id)
    val daysBefore = integer("days_before")
    override val primaryKey = PrimaryKey(dinninghallId)
}