package com.siral.data.services

import com.siral.data.database.tables.Schedule
import com.siral.data.interfaces.ScheduleDataSource
import com.siral.data.models.ScheduleItem
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate

class ScheduleService(private val db: Database): ScheduleDataSource {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAll(): List<ScheduleItem> = dbQuery {
        Schedule
            .selectAll()
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.itemDate],
                    time = it[Schedule.time],
                    dinninghallId = it[Schedule.dininghallId],
                    available = it[Schedule.available]
                )
            }
    }

    override suspend fun getById(id: Long): ScheduleItem? = dbQuery {
        Schedule
            .select { Schedule.id eq id }
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.itemDate],
                    time = it[Schedule.time],
                    dinninghallId = it[Schedule.dininghallId],
                    available = it[Schedule.available]
                )
            }
            .singleOrNull()
    }

    override suspend fun getByDinningHall(dinninghallId: Long): List<ScheduleItem> = dbQuery {
        Schedule
            .select {
                (Schedule.dininghallId eq dinninghallId) and
                        (Schedule.itemDate greaterEq LocalDate.now())
            }
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.itemDate],
                    time = it[Schedule.time],
                    dinninghallId = it[Schedule.dininghallId],
                    available = it[Schedule.available]
                )
            }

    }

    override suspend fun getAvailableItemsForDate(date: LocalDate, dinninghallId: Long): List<ScheduleItem> = dbQuery {
        Schedule
            .select {
                (Schedule.dininghallId eq dinninghallId) and
                        (Schedule.itemDate eq date) and
                        (Schedule.available eq true)
            }
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.itemDate],
                    time = it[Schedule.time],
                    dinninghallId = it[Schedule.dininghallId],
                    available = it[Schedule.available]
                )
            }
    }

    override suspend fun create(date: LocalDate, time: String, dinninghallId: Long): Unit = dbQuery {
        Schedule
            .insert {
                it[Schedule.itemDate] = date
                it[Schedule.time] = time
                it[Schedule.dininghallId] = dinninghallId
            }
    }

    override suspend fun delete(id: Long): Unit = dbQuery {
        Schedule.deleteWhere { Schedule.id eq id }
    }

}