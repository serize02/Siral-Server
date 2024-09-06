package com.siral.data

import com.siral.data.database.DatabaseInitializer
import com.siral.data.database.tables.Dinninghalls
import com.siral.data.database.tables.SiteManagerSchedulers
import com.siral.data.database.tables.Students
import com.siral.data.interfaces.VerifyDataSource
import com.siral.data.services.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DataService(db: Database): VerifyDataSource {

    val studentService = StudentService(db)
    val dinningHallService = DinningHallService(db)
    val siteManagerSchedulerService = SiteManagerSchedulerService(db)
    val scheduleService = ScheduleService(db)
    val reservationService = ReservationService(db)
    val logsService = LogsService(db)

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    init {
        DatabaseInitializer.init(db)
    }

    override suspend fun verifyExistentEmail(email: String): Boolean {
        val emails = dbQuery {
            Students
                .select { Students.email eq email }
                .count()
                +
            SiteManagerSchedulers
                .select { SiteManagerSchedulers.email eq email }
                .count()
        }
        return emails > 0
    }

    override suspend fun verifyExistentDinninghall(dinninghallName: String): Boolean = dbQuery {
        Dinninghalls
            .select { Dinninghalls.name eq dinninghallName }
            .count() > 0
    }

}
