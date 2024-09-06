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

    override suspend fun getScheduleItemById(scheduleItemId: Long): ScheduleItem? = dbQuery {
        Schedule
            .select { Schedule.id eq scheduleItemId }
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.itemDate],
                    time = it[Schedule.time],
                    dinninghallId = it[Schedule.dinninghallId],
                    available = it[Schedule.available]
                )
            }
            .singleOrNull()
    }

    override suspend fun insertScheduleItem(date: LocalDate, time: String, dinninghallID: Long): Unit = dbQuery {
        Schedule
            .insert {
                it[Schedule.itemDate] = date
                it[Schedule.time] = time
                it[Schedule.dinninghallId] = dinninghallID
            }
    }

    override suspend fun deleteScheduleItem(date: LocalDate, time: String, dinninghallID: Long): Unit = dbQuery {
        Schedule
            .deleteWhere {
                (itemDate eq date) and
                        (Schedule.time eq time) and
                        (dinninghallId eq dinninghallID)
            }
    }

    override suspend fun getSchedule(dinninghallID: Long): List<ScheduleItem> = dbQuery {
        Schedule
            .select {
                (Schedule.dinninghallId eq dinninghallID) and
                        (Schedule.itemDate greaterEq LocalDate.now())
            }
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.itemDate],
                    time = it[Schedule.time],
                    dinninghallId = it[Schedule.dinninghallId],
                    available = it[Schedule.available]
                )
            }

    }

    override suspend fun getScheduleItem(date: LocalDate, time: String, dinninghallID: Long): ScheduleItem? = dbQuery {
        Schedule
            .select {
                (Schedule.itemDate eq date) and
                        (Schedule.time eq time) and
                        (Schedule.dinninghallId eq dinninghallID)
            }
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.itemDate],
                    time = it[Schedule.time],
                    dinninghallId = it[Schedule.dinninghallId],
                    available = it[Schedule.available]
                )
            }
            .singleOrNull()
    }
}