package com.siral.data.services


import com.siral.data.database.tables.Reservations
import com.siral.data.models.Reservation
import com.siral.data.interfaces.ReservationDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class ReservationService(private val db: Database): ReservationDataSource {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAll(): List<Reservation> = dbQuery {
        Reservations
            .selectAll()
            .map {
                Reservation(
                    id = it[Reservations.id],
                    studentID = it[Reservations.studentID],
                    scheduleItemID = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.createdAt]
                )
            }
    }

    override suspend fun getById(id: Long): Reservation? = dbQuery {
        Reservations
            .select { Reservations.id eq id }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    studentID = it[Reservations.studentID],
                    scheduleItemID = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.createdAt]
                )
            }
            .singleOrNull()
    }

    override suspend fun getByStudent(studentId: Long): List<Reservation> = dbQuery {
        Reservations
            .select { Reservations.studentID eq studentId }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    studentID = it[Reservations.studentID],
                    scheduleItemID = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.createdAt]
                )
            }
            .toList()
    }

    override suspend fun getByScheduleItemIdAndUserId(studentId: Long, scheduleItemId: Long): Reservation? = dbQuery {
        Reservations
            .select {
                (Reservations.studentID eq studentId) and
                (Reservations.scheduleItemId eq scheduleItemId)
            }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    studentID = it[Reservations.studentID],
                    scheduleItemID = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.createdAt]
                )
            }
            .singleOrNull()
    }

    override suspend fun create(studentId: Long, scheduleItemId: Long): Long = dbQuery {
        Reservations
            .insert {
                it[Reservations.studentID] = studentId
                it[Reservations.scheduleItemId] = scheduleItemId
            } get Reservations.id
    }

    override suspend fun delete(id: Long): Unit = dbQuery {
        Reservations
            .deleteWhere { this.id eq id }
    }
}