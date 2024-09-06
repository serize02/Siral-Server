package com.siral.data.interfaces

import com.siral.data.models.DinningHall

interface DinningHallDataSource {
    suspend fun getDinninghallByID(dinninghallID: Long): DinningHall?
    suspend fun insertDinninghall(dinninghallName: String)
    suspend fun deleteDinninghall(dinninghallID: Long)
    suspend fun getDinninghallByName(dinninghallName: String): DinningHall?
}