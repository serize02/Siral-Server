package com.siral.data.student

import com.siral.responses.StudentData

interface StudentDataSource {
    suspend fun getStudentByEmail(email: String): Student?
    suspend fun getStudentById(studentId: Long): Student?
    suspend fun insertStudent(student: StudentData): Long
}