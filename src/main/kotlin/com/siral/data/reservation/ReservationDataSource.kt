package com.siral.data.reservation


interface ReservationDataSource {
    suspend fun makeReservation(studentID: Long, scheduleItemId: Long): Long
    suspend fun getReservationByScheduleItemIdAndUserId(studentID: Long, scheduleItemId: Long): Reservation?
    suspend fun deleteReservation(reservationId: Long)
    suspend fun getReservations(studentID: Long): List<Reservation>
    suspend fun getReservationByID(reservationId: Long): Reservation?
}