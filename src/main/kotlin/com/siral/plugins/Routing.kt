package com.siral.plugins

import com.siral.data.UserService
import com.siral.routes.*
import com.siral.security.token.TokenConfig
import com.siral.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userService: UserService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        addAdmins(userService)
        login(userService, tokenService, tokenConfig)
        auth()
        insertMeals(userService)
        getMealsForNextDays(userService)
        getMealById(userService)
        insertReservations(userService)
        deleteReservation(userService)
        getReservations(userService)
    }
}
