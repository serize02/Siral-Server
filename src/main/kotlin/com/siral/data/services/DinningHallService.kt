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

    override suspend fun getByName(name: String): DinningHall? = dbQuery {
        Dinninghalls
            .select { Dinninghalls.name eq name }
            .map {
                DinningHall(
                    id = it[id],
                    name = it[Dinninghalls.name]
                )
            }
            .singleOrNull()
    }

    override suspend fun getById(id: Long): DinningHall? = dbQuery {
        Dinninghalls
            .select { Dinninghalls.id eq id }
            .map {
                DinningHall(
                    id = it[Dinninghalls.id],
                    name = it[Dinninghalls.name]
                )
            }
            .singleOrNull()
    }

    override suspend fun create(name: String): Unit = dbQuery{
        val id = Dinninghalls.insert { it[this.name] = name } get Dinninghalls.id
        AvailabilityConfigs.insert { it[dinninghallId] = id }
    }

    override suspend fun update(id: Long, newName: String): Unit = dbQuery {
        Dinninghalls
            .update({Dinninghalls.id eq id}){
                it[name] = newName
            }
    }

    override suspend fun delete(id: Long): Unit = dbQuery {
        Dinninghalls.deleteWhere { this.id eq id }
    }
}