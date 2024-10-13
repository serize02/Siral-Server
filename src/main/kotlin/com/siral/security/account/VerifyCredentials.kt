package com.siral.security.account

import com.siral.request.AuthCredentials
import com.siral.responses.StudentData
import com.siral.utils.Admin
import java.util.Random

fun verifyStudentCredentials(credentials: AuthCredentials): StudentData {

    val random = Random()

    val data =  StudentData(
        name = "test-student",
        code = random.nextLong(123, 1000000),
        email = credentials.email,
        resident = random.nextBoolean()
    )

    return data
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