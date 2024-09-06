package com.siral

import com.siral.data.database.DatabaseFactory
import com.siral.data.DataService
import com.siral.plugins.*
import com.siral.security.token.JwtTokenService
import com.siral.security.token.TokenConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val database = DatabaseFactory.init()
    val dataService = DataService(database)
    val tokenService = JwtTokenService()

    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 24L * 60L * 60L * 500L,
        secret = System.getenv("jwt-secret")
    )

    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(dataService, tokenService, tokenConfig)
    configureMonitoring()

}
