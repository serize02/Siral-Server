package com.siral.data.logs

import com.siral.utils.Actions
import com.siral.utils.Status

interface LogsDataSource {
    suspend fun getLogs(): List<Log>
    suspend fun addLog(email: String, action: Actions, status: Status)
}