package com.siral.security.account

import com.siral.request.AuthCredentials
import com.siral.responses.StudentData
import com.siral.utils.Admin

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


    if(credentials.email != Admin.email)
        return false
    if(credentials.password != Admin.password)
        return false
    return true
}

fun verifySiteManagerSchedulerCredentials(
    credentials: AuthCredentials,
): Boolean {
    //verify email and password in the accounts system
    return true
}