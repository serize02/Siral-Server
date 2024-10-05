package com.siral.data

import com.siral.data.database.DatabaseInitializer
import com.siral.data.database.tables.*
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

    suspend fun getData(): List<Data> {
        val result = mutableListOf<Data>()
        val today = java.time.LocalDate.now()

        for(i in 1..30){
            val date = today.plusDays(i.toLong())
            dbQuery {
                Dinninghalls.selectAll().forEach { dinninghall ->
                    val dinninghallName = dinninghall[Dinninghalls.name]
                    val count = Reservations
                        .innerJoin(Schedule)
                        .select {
                            (Schedule.dinninghallId eq dinninghall[Dinninghalls.id]) and
                            (Reservations.dateOfReservation greaterEq date.atStartOfDay()) and
                            (Reservations.dateOfReservation lessEq date.plusDays(1).atStartOfDay())
                        }
                        .count()
                    result.add(Data(date, dinninghallName, count))
                }
            }
        }

        return result
    }

}
