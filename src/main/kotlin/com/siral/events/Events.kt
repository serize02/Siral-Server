package com.siral.events

import com.siral.data.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun events(userService: UserService){
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        while (true){
            delay(60 * 1000)
            userService.activateMeals(2)
        }
    }
}