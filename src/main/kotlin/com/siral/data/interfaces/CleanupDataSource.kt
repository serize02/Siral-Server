package com.siral.data.interfaces

interface CleanupDataSource {
    suspend fun cleanExpiredReservations()

    suspend fun cleanOldLogs()

    suspend fun updateNoActiveStudents()

    suspend fun cleanOldStudents()

    suspend fun cleanOldScheduleItems()

    suspend fun updateAvailableScheduleItems()
}