package com.siral.cleanup

import com.siral.data.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun dbCleanUp(userService: UserService){
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        while (true){
            delay(60 * 1000)
            userService.activateScheduleItem(2)
            userService.updateActive()
            userService.deleteStudent()
        }
    }
}