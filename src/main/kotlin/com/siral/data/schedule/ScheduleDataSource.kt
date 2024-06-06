package com.siral.data.schedule

import com.siral.data.dinninghall.DinningHall
import com.siral.data.student.Student

interface ScheduleDataSource {
    suspend fun getScheduleForTheNextDays(dinningHallName: String): List<ScheduleItem>
    suspend fun getScheduleItemById(mealId: String): ScheduleItem?
    suspend fun getScheduleItem(scheduleItem: ScheduleItem): ScheduleItem?
    suspend fun insertScheduleItem(scheduleItem: ScheduleItem)
    suspend fun activateScheduleItem(days: Long)
}