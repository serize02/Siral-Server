package com.siral.data.student

interface StudentDataSource {
    suspend fun getStudentByUsername(username: String): Student?
    suspend fun getStudentById(userId: String): Student?
    suspend fun insertStudent(student: Student)
    suspend fun updateLast(userId: String)
    suspend fun updateActive()
    suspend fun deleteStudent()
    suspend fun deleteStudentByDinningHall(dinningHallName: String)
}