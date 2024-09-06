package com.siral

import com.siral.data.DatabaseFactory
import com.siral.data.UserService
import com.siral.plugins.*
import com.siral.security.token.JwtTokenService
import com.siral.security.token.TokenConfig
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val database = DatabaseFactory.init()

    val userService = UserService(database)

    val tokenService = JwtTokenService()

    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 24L * 60L * 60L * 500L,
        secret = System.getenv("jwt-secret")
    )

    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(userService, tokenService, tokenConfig)
    configureMonitoring()
}
