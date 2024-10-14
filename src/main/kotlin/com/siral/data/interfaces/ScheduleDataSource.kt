package com.siral.data.interfaces

import com.siral.data.models.ScheduleItem
import java.time.LocalDate

interface ScheduleDataSource {

    suspend fun getAll(): List<ScheduleItem>

    suspend fun getById(id: Long): ScheduleItem?

    suspend fun getByDinningHall(dinninghallId: Long): List<ScheduleItem>

//    suspend fun getByDateTimeDinningHall(date: LocalDate, time: String, dinninghallID: Long): ScheduleItem?

    suspend fun create(date: LocalDate, time: String, dinninghallId: Long)

    suspend fun delete(id: Long)

    suspend fun getAvailableItemsForDate(date: LocalDate, dinninghallId: Long): List<ScheduleItem>

}