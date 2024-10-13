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

    override suspend fun makeReservation(studentID: Long, scheduleItemId: Long): Long = dbQuery {
        Reservations
            .insert {
                it[Reservations.studentID] = studentID
                it[Reservations.scheduleItemId] = scheduleItemId
            } get Reservations.id
    }

    override suspend fun getReservationByScheduleItemIdAndUserId(studentID: Long, scheduleItemID: Long): Reservation? = dbQuery {
        Reservations
            .select {
                (Reservations.studentID eq studentID) and
                        (Reservations.scheduleItemId eq scheduleItemID)
            }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    studentID = it[Reservations.studentID],
                    scheduleItemID = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.dateOfReservation]
                )
            }
            .singleOrNull()
    }

    override suspend fun deleteReservation(reservationId: Long): Unit = dbQuery {
        Reservations
            .deleteWhere { id eq reservationId }
    }

    override suspend fun getReservations(studentID: Long): List<Reservation> = dbQuery {
        Reservations
            .select { Reservations.studentID eq studentID }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    studentID = it[Reservations.studentID],
                    scheduleItemID = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.dateOfReservation]
                )
            }
            .toList()
    }

    override suspend fun getReservationByID(reservationId: Long): Reservation? = dbQuery {
        Reservations
            .select { Reservations.id eq reservationId }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    studentID = it[Reservations.studentID],
                    scheduleItemID = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.dateOfReservation]
                )
            }
            .singleOrNull()
    }

    override suspend fun getAll(): List<Reservation> = dbQuery {
        Reservations
            .selectAll()
            .map {
                Reservation(
                    id = it[Reservations.id],
                    studentID = it[Reservations.studentID],
                    scheduleItemID = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.dateOfReservation]
                )
            }
    }
}