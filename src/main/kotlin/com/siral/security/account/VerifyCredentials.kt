package com.siral.security.account

import com.siral.data.UserService
import com.siral.request.AuthCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun verifyCredentials(
    credentials: AuthCredentials,
    userService: UserService
): Boolean {
    val scope = CoroutineScope(Dispatchers.Default)
    var validCredentials = false
    scope.launch {
        val foundDinningHall = userService.getDinningHallByName(credentials.dinningHall)
        //    TODO("verify username and password with sigenu api")
        val validUsernameAndPassword = true
        validCredentials = foundDinningHall!=null && validUsernameAndPassword
    }
    return validCredentials
}