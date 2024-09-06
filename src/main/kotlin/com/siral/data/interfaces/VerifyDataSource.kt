package com.siral.data.interfaces

interface VerifyDataSource {
    suspend fun verifyExistentEmail(email: String): Boolean
    suspend fun verifyExistentDinninghall(dinninghallName: String): Boolean
}