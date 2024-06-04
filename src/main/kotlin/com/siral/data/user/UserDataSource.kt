package com.siral.data.user

interface UserDataSource {
    suspend fun getUserByUsername(username: String): User?
    suspend fun getUserById(userId: String): User?
    suspend fun insertUser(user: User)
    suspend fun updateLast(userId: String)
    suspend fun updateActive()
    suspend fun deleteUser()
}