package com.siral.data.schedule

import java.time.LocalDate

interface ScheduleDataSource {
    suspend fun getScheduleItemById(scheduleItemId: Long): ScheduleItem?
    suspend fun insertScheduleItem(date: LocalDate, time: String, dinninghallID: Long)
    suspend fun deleteScheduleItem(date: LocalDate, time: String, dinninghallID: Long)
    suspend fun getSchedule(dinninghallID: Long): List<ScheduleItem>
    suspend fun getScheduleItem(date: LocalDate, time: String, dinninghallID: Long): ScheduleItem?
}