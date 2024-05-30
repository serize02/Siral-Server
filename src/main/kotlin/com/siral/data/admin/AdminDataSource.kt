package com.siral.data.admin

interface AdminDataSource {
    suspend fun insertAdmin(userName: String)
    suspend fun verifyAdmin(userName: String): Boolean
}