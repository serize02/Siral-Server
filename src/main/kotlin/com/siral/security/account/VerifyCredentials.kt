package com.siral.security.account

import com.siral.data.UserService
import com.siral.request.AuthCredentials
import com.siral.request.StudentAuthCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun verifyStudentCredentials(
    credentials: StudentAuthCredentials,
    userService: UserService
): Boolean {
//    TODO("Find the way to verify also this")
//    val foundDinningHall = userService.getDinningHallByName(credentials.dinningHall)
//    val validUsernameAndPassword = TODO("Check at the sigenu api")
    return true
}

fun verifyAdminCredentials(
    credentials: AuthCredentials,
    userService: UserService
): Boolean {
//    TODO("Find the way to verify also this")
//    val admin = userService.getAdminByName(credentials.username)
//    val validUsernameAndPassword = TODO("Check at the asset api")
    return true
}