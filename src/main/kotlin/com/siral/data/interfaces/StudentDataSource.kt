package com.siral.data.interfaces

import com.siral.data.models.Student
import com.siral.responses.StudentData

interface StudentDataSource {
    suspend fun getStudentByEmail(email: String): Student?
    suspend fun getStudentById(studentId: Long): Student?
    suspend fun insertStudent(student: StudentData): Long
}