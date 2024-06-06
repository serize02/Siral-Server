package com.siral.data.admin

interface AdminDataSource {
    suspend fun insertAdmin(admin: Admin)
    suspend fun getAdminByName(userName: String): Admin?
    suspend fun getAdminById(adminId: String): Admin?
}