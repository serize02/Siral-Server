package com.siral

import com.siral.data.UserService
import com.siral.events.events
import com.siral.plugins.*
import com.siral.security.token.JwtTokenService
import com.siral.security.token.TokenConfig
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/siraldb",
        user = System.getenv("dbuser"),
        driver = "org.postgresql.Driver",
        password = System.getenv("dbpassword")
    )

    val userService = UserService(database)

    val tokenService = JwtTokenService()

    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expiresIn = 365L * 24L * 60L * 60L * 500L,
        secret = System.getenv("JWT_SECRET")
    )

    events(userService)

    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(userService, tokenService, tokenConfig)
    configureMonitoring()

}
