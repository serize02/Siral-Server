package com.siral.data.database.cleanup

import com.siral.data.database.tables.Logs
import com.siral.data.database.tables.Reservations
import com.siral.data.interfaces.CleanupDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate

class CleanupService(): CleanupDataSource {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun cleanExpiredReservations(): Unit = dbQuery {
        Reservations.deleteWhere { Reservations.dateOfReservation less LocalDate.now().atStartOfDay() }
    }

    override suspend fun cleanOldLogs(): Unit = dbQuery {
        Logs.deleteWhere { Logs.timestamp less (LocalDate.now().minusMonths(1).atStartOfDay())}
    }
}