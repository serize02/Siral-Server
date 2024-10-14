package com.siral.data.interfaces

import com.siral.data.models.DinningHall

interface DinningHallDataSource {

    suspend fun getAll(): List<DinningHall>

    suspend fun getById(id: Long): DinningHall?

    suspend fun getByName(name: String): DinningHall?

    suspend fun create(name: String)

    suspend fun update(id: Long, newName: String)

    suspend fun delete(id: Long)

}