package com.siral.data.reservation

interface ReservationDataSource {
    suspend fun insertReservation(reservation: Reservation)
    suspend fun deleteReservation(reservationId: String)
    suspend fun getReservations(userId: String): List<Reservation>
    suspend fun getReservationByMealdId(mealId: String): Reservation?
}