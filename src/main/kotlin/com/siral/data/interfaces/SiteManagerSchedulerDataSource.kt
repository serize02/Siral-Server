package com.siral.data.interfaces

import com.siral.data.models.SiteManagerScheduler
import com.siral.request.CreateSiteManagerScheduler

interface SiteManagerSchedulerDataSource {

    suspend fun getAll(): List<SiteManagerScheduler>

    suspend fun getByEmail(email: String): SiteManagerScheduler?

    suspend fun getByID(id: Long): SiteManagerScheduler?

    suspend fun create(credentials: CreateSiteManagerScheduler)

    suspend fun delete(id: Long)

    suspend fun updateDaysBeforeReservation(dinninghallId: Long, days: Int)

}