package com.siral.data.interfaces

import com.siral.data.models.Log
import com.siral.utils.Actions
import com.siral.utils.Status

interface LogsDataSource {
    suspend fun getAll(): List<Log>
    suspend fun create(email: String, action: Actions, status: Status)
}