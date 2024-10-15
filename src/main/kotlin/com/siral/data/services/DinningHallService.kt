package com.siral.data.services


import com.siral.data.database.tables.*
import com.siral.data.database.tables.Dininghalls.id
import com.siral.data.models.DinningHall
import com.siral.data.interfaces.DinningHallDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DinningHallService(private val db: Database): DinningHallDataSource {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAll(): List<DinningHall> = dbQuery {
        Dininghalls
            .selectAll()
            .map {
                DinningHall(
                    id = it[id],
                    name = it[Dininghalls.name]
                )
            }
    }

    override suspend fun getByName(name: String): DinningHall? = dbQuery {
        Dininghalls
            .select { Dininghalls.name eq name }
            .map {
                DinningHall(
                    id = it[id],
                    name = it[Dininghalls.name]
                )
            }
            .singleOrNull()
    }

    override suspend fun getById(id: Long): DinningHall? = dbQuery {
        Dininghalls
            .select { Dininghalls.id eq id }
            .map {
                DinningHall(
                    id = it[Dininghalls.id],
                    name = it[Dininghalls.name]
                )
            }
            .singleOrNull()
    }

    override suspend fun create(name: String): Unit = dbQuery{
        val id = Dininghalls.insert { it[this.name] = name } get Dininghalls.id
        AvailabilityConfigs.insert { it[dininghallId] = id }
    }

    override suspend fun update(id: Long, newName: String): Unit = dbQuery {
        Dininghalls
            .update({Dininghalls.id eq id}){
                it[name] = newName
            }
    }

    override suspend fun delete(id: Long): Unit = dbQuery {
        Dininghalls.deleteWhere { this.id eq id }
    }
}