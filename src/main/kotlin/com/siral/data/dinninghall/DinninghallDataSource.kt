package com.siral.data.dinninghall

interface DinninghallDataSource {
    suspend fun getDinninghallByID(dinninghallID: Long): DinningHall?
    suspend fun insertDinninghall(dinninghallName: String)
    suspend fun deleteDinninghall(dinninghallID: Long)
}