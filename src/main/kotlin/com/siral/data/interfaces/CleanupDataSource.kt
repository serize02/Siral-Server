package com.siral.data.interfaces

interface CleanupDataSource {
    suspend fun cleanExpiredReservations()
    suspend fun cleanOldLogs()
}