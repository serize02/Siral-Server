package com.siral.plugins

import com.siral.data.DataService
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
    dataService: DataService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    routing {

        studentLogin(dataService, tokenService, tokenConfig)
        adminLogin(dataService, tokenService, tokenConfig)
        auth()
        siteManagerSchedulerLogin(dataService, tokenService, tokenConfig)


        authenticate {
            getSchedule(dataService)

            insertDinningHalls(dataService)
            deleteDinningHall(dataService)
            insertNewRole(dataService)
            deleteRole(dataService)

            insertScheduleItem(dataService)
            deleteScheduleItem(dataService)
            daysBefore(dataService)

            makeReservations(dataService)
            deleteReservation(dataService)
            getStudentReservations(dataService)

        }
    }
}

suspend inline fun ApplicationCall.withRole(role: UserRole, block :() -> Unit){
    val principal = principal<JWTPrincipal>()
    val userRole = principal?.payload?.getClaim("userRole")?.asString()
    if(userRole == role.name) block() else respond(HttpStatusCode.Forbidden, ResponseMessage.ACCESS_DENIED)
}

