package com.siral.data.student

interface StudentDataSource {
    suspend fun getUserByUsername(username: String): Student?
    suspend fun getUserById(userId: String): Student?
    suspend fun insertUser(student: Student)
    suspend fun updateLast(userId: String)
    suspend fun updateActive()
    suspend fun deleteUser()
}