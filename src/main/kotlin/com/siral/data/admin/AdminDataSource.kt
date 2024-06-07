package com.siral.data.admin

import org.jetbrains.annotations.ApiStatus.Experimental

interface AdminDataSource {
    @Experimental
    suspend fun insertAdmin(admin: Admin)
    suspend fun getAdminByName(userName: String): Admin?
    suspend fun getAdminById(adminId: String): Admin?
}