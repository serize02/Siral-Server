package com.siral.request

import com.siral.responses.StudentData
import kotlinx.serialization.Serializable
import java.util.Random

@Serializable
class AuthCredentials(
    val email: String,
    val password: String,
){
    fun retrieveData(): StudentData {
        val random = Random()

        return StudentData(
            name = "student-test",
            code = random.nextLong(123, 1000000),
            email = this.email,
            resident = random.nextBoolean()
        )
    }
}
