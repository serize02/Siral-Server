package com.siral.data.interfaces

import com.siral.data.models.Student
import com.siral.responses.StudentData

interface StudentDataSource {

    suspend fun getAll(): List<Student>

    suspend fun getById(id: Long): Student?

    suspend fun getByEmail(email: String): Student?

    suspend fun create(student: StudentData): Long

    suspend fun updateLastAndActive(id: Long)

    suspend fun updateLastReservation(id: Long, reservationId: Long)

    suspend fun delete(id: Long)

}