package com.siral.data.interfaces

import com.siral.data.models.SiteManagerScheduler
import com.siral.request.NewRoleCredentials

interface SiteManagerSchedulerDataSource {
    suspend fun insertNewSiteManagerScheduler(credentials: NewRoleCredentials)
    suspend fun deleteSiteManagerScheduler(email: String)
    suspend fun getSiteManagerSchedulerByEmail(email: String): SiteManagerScheduler?
    suspend fun getSiteManagerSchedulerByID(id: Long): SiteManagerScheduler?
}