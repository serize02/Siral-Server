package com.siral.data.interfaces

import com.siral.data.models.Reservation


interface ReservationDataSource {

    suspend fun getAll(): List<Reservation>

    suspend fun getById(id: Long): Reservation?

    suspend fun getByStudent(studentId: Long): List<Reservation>

    suspend fun getByScheduleItemIdAndUserId(studentId: Long, scheduleItemId: Long): Reservation?

    suspend fun create(studentId: Long, scheduleItemId: Long): Long

    suspend fun delete(id: Long)

}