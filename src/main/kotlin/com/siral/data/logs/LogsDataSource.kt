package com.siral.data.logs

import com.siral.utils.Actions

interface LogsDataSource {
    suspend fun getLogs(): List<Log>
    suspend fun addLog(email: String, action: Actions)
}