package com.siral.security.account

import com.siral.data.UserService
import com.siral.request.AuthCredentials
import com.siral.responses.StudentData

fun verifyStudentCredentials(credentials: AuthCredentials): StudentData? {
    // Fake credentials for validation
    val fakeEmail = "student@example.com"
    val fakePassword = "password123"

    return if (credentials.email == fakeEmail && credentials.password == fakePassword) {
        // Return fake student data
        StudentData(
            name = "John Doe",
            code = 12345L,
            email = fakeEmail,
            resident = true,
        )
    } else {
        // Return null if credentials do not match
        null
    }
}

fun verifyAdminCredentials(
    credentials: AuthCredentials,
): Boolean {

    val email = System.getenv("adminemail")
    val pd = System.getenv("adminpd")

    if(credentials.email != email)
        return false
    if(credentials.password != pd)
        return false
    return true
}