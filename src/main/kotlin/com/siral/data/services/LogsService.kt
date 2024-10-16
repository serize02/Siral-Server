package com.siral.data.services


import com.siral.data.database.tables.Logs
import com.siral.data.models.Log
import com.siral.data.interfaces.LogsDataSource
import com.siral.utils.Actions
import com.siral.utils.Status
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class LogsService(private val db: Database): LogsDataSource {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAll(): List<Log> = dbQuery {
        Logs
            .selectAll()
            .orderBy(Logs.timestamp, SortOrder.DESC)
            .map {
                Log(
                    id = it[Logs.id],
                    email = it[Logs.email],
                    action = it[Logs.action],
                    status = it[Logs.status],
                    timestamp = it[Logs.timestamp]
                )
            }
    }

    override suspend fun create(email: String, action: Actions, status: Status): Unit = dbQuery {
        Logs
            .insert {
                it[Logs.email] = email
                it[Logs.action] = action.name
                it[Logs.status] = status.name
            }
    }
}