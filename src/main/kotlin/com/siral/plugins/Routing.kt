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
import org.jetbrains.annotations.ApiStatus.Experimental

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
        addAdmins(userService)

        adminLogin(userService, tokenService, tokenConfig)
        studentLogin(userService, tokenService, tokenConfig)

        auth()

        insertDinningHalls(userService)
        deleteDinningHall(userService)

        insertScheduleItem(userService)
        getScheduleForNextDays(userService)

        insertReservations(userService)
        deleteReservation(userService)
        getStudentReservations(userService)
    }
}
