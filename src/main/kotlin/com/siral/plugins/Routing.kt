package com.siral.plugins

import com.siral.data.UserService
import com.siral.routes.*
import com.siral.security.token.TokenConfig
import com.siral.security.token.TokenService
import com.siral.utils.ResponseMessage
import com.siral.utils.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
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
        siteManagerSchedulerLogin(userService, tokenService, tokenConfig)


        authenticate {
            getSchedule(userService)

            insertDinningHalls(userService)
            deleteDinningHall(userService)
            insertNewRole(userService)
            deleteRole(userService)

            insertScheduleItem(userService)
            deleteScheduleItem(userService)

            makeReservations(userService)
            deleteReservation(userService)
            getStudentReservations(userService)

        }
    }
}

suspend inline fun ApplicationCall.withRole(role: UserRole, block :() -> Unit){
    val principal = principal<JWTPrincipal>()
    val userRole = principal?.payload?.getClaim("userRole")?.asString()
    if(userRole == role.name) block() else respond(HttpStatusCode.Forbidden, ResponseMessage.ACCESS_DENIED)
}

