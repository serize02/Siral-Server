package com.siral.data

interface EventsDataSource {
    suspend fun activateMeals(days: Long)
}