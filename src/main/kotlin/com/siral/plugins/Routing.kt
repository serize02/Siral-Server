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
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        studentLogin(userService, tokenService, tokenConfig)
        adminLogin(userService, tokenService, tokenConfig)
        auth()
        insertDinningHalls(userService)
        deleteDinningHall(userService)
        insertScheduleItem(userService)
        deleteScheduleItem(userService)
        getSchedule(userService)
        makeReservations(userService)
        deleteReservation(userService)
        getStudentReservations(userService)
        insertNewRole(userService)
        deleteRole(userService)
        siteManagerSchedulerLogin(userService, tokenService, tokenConfig)
    }
}
