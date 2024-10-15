package com.siral.data.services

import com.siral.data.database.tables.AvailabilityConfigs
import com.siral.data.database.tables.SiteManagerSchedulers
import com.siral.data.models.SiteManagerScheduler
import com.siral.data.interfaces.SiteManagerSchedulerDataSource
import com.siral.request.CreateSiteManagerScheduler
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class SiteManagerSchedulerService(private val db: Database): SiteManagerSchedulerDataSource {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAll(): List<SiteManagerScheduler> = dbQuery {
        SiteManagerSchedulers
            .selectAll()
            .map {
                SiteManagerScheduler(
                    id = it[SiteManagerSchedulers.id],
                    email = it[SiteManagerSchedulers.email],
                    dinninghallID = it[SiteManagerSchedulers.dininghallID],
                    role = it[SiteManagerSchedulers.role]
                )
            }
    }

    override suspend fun getByID(id: Long): SiteManagerScheduler? = dbQuery {
        SiteManagerSchedulers
            .select { SiteManagerSchedulers.id eq id }
            .map {
                SiteManagerScheduler(
                    id = it[SiteManagerSchedulers.id],
                    email = it[SiteManagerSchedulers.email],
                    dinninghallID = it[SiteManagerSchedulers.dininghallID],
                    role = it[SiteManagerSchedulers.role]
                )
            }
            .singleOrNull()
    }

    override suspend fun getByEmail(email: String): SiteManagerScheduler? = dbQuery {
        SiteManagerSchedulers
            .select { SiteManagerSchedulers.email eq email }
            .map {
                SiteManagerScheduler(
                    id = it[SiteManagerSchedulers.id],
                    email = it[SiteManagerSchedulers.email],
                    dinninghallID = it[SiteManagerSchedulers.dininghallID],
                    role = it[SiteManagerSchedulers.role]
                )
            }
            .singleOrNull()
    }

    override suspend fun create(credentials: CreateSiteManagerScheduler): Unit = dbQuery {
        SiteManagerSchedulers
            .insert {
                it[email] = credentials.email
                it[dininghallID] = credentials.dininghallId
                it[role] = credentials.role
            }
    }

    override suspend fun delete(id: Long): Unit = dbQuery {
        SiteManagerSchedulers
            .deleteWhere { SiteManagerSchedulers.id eq id}
    }

    override suspend fun updateDaysBeforeReservation(dinninghallId: Long, days: Int): Unit = dbQuery {
        AvailabilityConfigs
            .update({ AvailabilityConfigs.dininghallId eq dinninghallId }) {
                it[daysBefore] = days
            }
    }

}