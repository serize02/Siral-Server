package com.siral

import com.siral.data.database.DatabaseFactory
import com.siral.data.DataService
import com.siral.data.database.cleanup.CleanerHandler
import com.siral.data.database.cleanup.CleanupService
import com.siral.plugins.*
import com.siral.security.token.JwtTokenService
import com.siral.security.token.TokenConfig
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val DAY_DELAY = 24L * 60 * 60 * 1000

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val dotenv = dotenv()

    val database = DatabaseFactory.init()

    val dataService = DataService(database)

    val cleanupService = CleanupService()

    val cleaner = CleanerHandler(listOf(
        { cleanupService.cleanOldLogs() },
        { cleanupService.cleanExpiredReservations() },
        { cleanupService.cleanOldStudents() },
        { cleanupService.updateNoActiveStudents() },
        { cleanupService.updateAvailableScheduleItems() }
    ))

    launch(Dispatchers.Default) {
        cleaner.start(DAY_DELAY)
    }

    val tokenService = JwtTokenService()

    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = DAY_DELAY,
        secret = dotenv["JWT_SECRET"]
    )

    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(dataService, tokenService, tokenConfig)
    configureMonitoring()
}
