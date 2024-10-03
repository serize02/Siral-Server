package com.siral.data.database.cleanup

import kotlinx.coroutines.*

class CleanerHandler(private val tasks:  List<suspend () -> Unit>) {
    private val scope = CoroutineScope(Dispatchers.Default +
            CoroutineExceptionHandler { context, error ->
                error.printStackTrace()
            }
    )

    private suspend fun clean() = coroutineScope {
        supervisorScope {
            tasks.forEach { task ->
                launch {
                    println(Thread.currentThread().name)
                    task()
                }
            }
        }
    }

    fun start(delay: Long) = scope.launch {
        while (true) {
            clean()
            delay(delay)
        }
    }
}