package com.siral.data.services


import com.siral.data.database.tables.*
import com.siral.data.database.tables.Dinninghalls.id
import com.siral.data.models.DinningHall
import com.siral.data.interfaces.DinningHallDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DinningHallService(private val db: Database): DinningHallDataSource {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getDinninghallByID(dinninghallID: Long): DinningHall? = dbQuery {
        Dinninghalls
            .select { Dinninghalls.id eq dinninghallID }
            .map {
                DinningHall(
                    id = it[Dinninghalls.id],
                    name = it[Dinninghalls.name]
                )
            }
            .singleOrNull()
    }

    override suspend fun insertDinninghall(dinninghallName: String): Unit = dbQuery{
        val id = Dinninghalls.insert { it[name] = dinninghallName } get Dinninghalls.id
        AvailabilityConfigs.insert { it[dinninghallId] = id }
    }

    override suspend fun deleteDinninghall(dinninghallID: Long): Unit = dbQuery {
        SiteManagerSchedulers.deleteWhere { SiteManagerSchedulers.dinninghallID eq dinninghallID }
        AvailabilityConfigs.deleteWhere { dinninghallId eq dinninghallID }
        Schedule
            .select { Schedule.dinninghallId eq dinninghallID }
            .toList()
            .forEach { item ->
                Reservations.deleteWhere { Reservations.scheduleItemId eq item[Schedule.id] }
            }
        Schedule.deleteWhere { Schedule.dinninghallId eq dinninghallID }
        Dinninghalls.deleteWhere { id eq dinninghallID }
    }

    override suspend fun getDinninghallByName(dinninghallName: String): DinningHall? = dbQuery {
        Dinninghalls
            .select { Dinninghalls.name eq dinninghallName }
            .map {
                DinningHall(
                    id = it[id],
                    name = it[Dinninghalls.name]
                )
            }
            .singleOrNull()
    }

    override suspend fun getAll(): List<DinningHall> = dbQuery {
        Dinninghalls
            .selectAll()
            .map {
                DinningHall(
                    id = it[id],
                    name = it[Dinninghalls.name]
                )
            }
    }
}