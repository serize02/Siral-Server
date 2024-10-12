package com.siral.plugins

import com.siral.data.DataService
import com.siral.responses.Response
import com.siral.routes.*
import com.siral.security.token.TokenConfig
import com.siral.security.token.TokenService
import com.siral.utils.ResponseMessage
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
        getSchedule(dataService)
//        siteManagerSchedulerLogin(dataService, tokenService, tokenConfig)

        authenticate {

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

            getAllLogs(dataService)
            getStatsData(dataService)

        }
    }
}

suspend inline fun ApplicationCall.withRole(roles: List<String>, block :() -> Unit){
    val principal = principal<JWTPrincipal>()
    val userRole = principal?.payload?.getClaim("userRole")?.asString()
    if (userRole in roles) block() else respond(HttpStatusCode.Forbidden, Response(success = false, data = null, message = ResponseMessage.ACCESS_DENIED.name, status = 403))
}

