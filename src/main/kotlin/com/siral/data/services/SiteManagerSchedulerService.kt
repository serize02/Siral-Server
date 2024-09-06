package com.siral.data.services

import com.siral.data.database.tables.Dinninghalls
import com.siral.data.database.tables.SiteManagerSchedulers
import com.siral.data.models.SiteManagerScheduler
import com.siral.data.interfaces.SiteManagerSchedulerDataSource
import com.siral.request.NewRoleCredentials
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class SiteManagerSchedulerService(private val db: Database): SiteManagerSchedulerDataSource {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun insertNewSiteManagerScheduler(credentials: NewRoleCredentials): Unit = dbQuery {
        SiteManagerSchedulers
            .insert {
                it[email] = credentials.email
                it[dinninghallID] = Dinninghalls
                    .select { Dinninghalls.name eq credentials.dinninghall }
                    .single()[Dinninghalls.id]
                it[role] = credentials.role
            }
    }

    override suspend fun deleteSiteManagerScheduler(email: String): Unit = dbQuery {
        SiteManagerSchedulers
            .deleteWhere { SiteManagerSchedulers.email eq email }
    }

    override suspend fun getSiteManagerSchedulerByEmail(email: String): SiteManagerScheduler? = dbQuery {
        SiteManagerSchedulers
            .select { SiteManagerSchedulers.email eq email }
            .map {
                SiteManagerScheduler(
                    id = it[SiteManagerSchedulers.id],
                    email = it[SiteManagerSchedulers.email],
                    dinninghallID = it[SiteManagerSchedulers.dinninghallID],
                    role = it[SiteManagerSchedulers.role]
                )
            }
            .singleOrNull()
    }

    override suspend fun getSiteManagerSchedulerByID(id: Long): SiteManagerScheduler? = dbQuery {
        SiteManagerSchedulers
            .select { SiteManagerSchedulers.id eq id }
            .map {
                SiteManagerScheduler(
                    id = it[SiteManagerSchedulers.id],
                    email = it[SiteManagerSchedulers.email],
                    dinninghallID = it[SiteManagerSchedulers.dinninghallID],
                    role = it[SiteManagerSchedulers.role]
                )
            }
            .singleOrNull()
    }
}