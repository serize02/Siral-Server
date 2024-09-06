package com.siral.data.database.tables

import org.jetbrains.exposed.sql.Table


object AvailabilityConfigs: Table("availability_configs") {
    val dinninghallId = reference("dinninghall_id", Dinninghalls.id)
    val daysBefore = integer("days_before").default(30)
    override val primaryKey = PrimaryKey(dinninghallId)
}