package com.siral.data.database.cleanup

import com.siral.data.database.tables.*
import com.siral.data.interfaces.CleanupDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDate
import java.time.LocalDateTime

class CleanupService(): CleanupDataSource {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun cleanExpiredReservations(): Unit = dbQuery {
        Reservations.deleteWhere { Reservations.dateOfReservation lessEq LocalDate.now().atStartOfDay() }
    }

    override suspend fun cleanOldLogs(): Unit = dbQuery {
        Logs.deleteWhere { Logs.timestamp lessEq (LocalDate.now().minusMonths(1).atStartOfDay())}
    }

    override suspend fun updateNoActiveStudents(): Unit = dbQuery {
        Students.update({ Students.last lessEq (LocalDateTime.now().minusMonths(1)) }) { it[active] = false }
    }

    override suspend fun cleanOldStudents(): Unit = dbQuery {
        Students.deleteWhere { Students.last lessEq (LocalDateTime.now().minusMonths(6)) }
    }

    override suspend fun cleanOldScheduleItems(): Unit = dbQuery {
        Schedule.deleteWhere { Schedule.itemDate lessEq  LocalDate.now() }
    }

    override suspend fun updateAvailableScheduleItems(): Unit = dbQuery {
        val config = AvailabilityConfigs
            .selectAll()
            .associate { it[AvailabilityConfigs.dinninghallId] to it[AvailabilityConfigs.daysBefore] }
        for((dinningHallId, daysBefore) in config) {
            Schedule
                .update({ (Schedule.dinninghallId eq dinningHallId) and (Schedule.itemDate eq LocalDate.now().plusDays(daysBefore.toLong())) }) {
                    it[available] = true
                }
        }
    }
}