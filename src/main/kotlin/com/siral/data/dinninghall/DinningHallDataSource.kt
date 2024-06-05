package com.siral.data.dinninghall

interface DinningHallDataSource {
    suspend fun insertDinningHall(dinningHall: DinningHall)
    suspend fun deleteDinningHall(dinningHallId: String)
    suspend fun getDinningHallByName(dinningHallName: String): DinningHall?
}