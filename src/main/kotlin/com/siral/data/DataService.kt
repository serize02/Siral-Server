package com.siral.data

import com.siral.data.database.DatabaseInitializer
import com.siral.data.database.tables.*
import com.siral.data.interfaces.VerifyDataSource
import com.siral.data.services.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDate

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
        Dininghalls
            .select { Dininghalls.name eq dinninghallName }
            .count() > 0
    }

    suspend fun getStatsData(): List<Data> {
        val result = mutableListOf<Data>()
        val date = LocalDate.of(LocalDate.now().year, LocalDate.now().month, 1)

        for(i in 1..30){
            val day = date.plusDays(i.toLong())
            dbQuery {
                Dininghalls.selectAll().forEach { dininghall ->
                    val dininghallName = dininghall[Dininghalls.name]
                    val count = Reservations
                        .innerJoin(Schedule)
                        .select {
                            (Schedule.dininghallId eq dininghall[Dininghalls.id]) and
                            (Schedule.itemDate eq day) and
                            (Reservations.scheduleItemId eq Schedule.id)
                        }
                        .count()
                    result.add(Data(day, dininghallName, count))
                }
            }
        }

        return result
    }

}
