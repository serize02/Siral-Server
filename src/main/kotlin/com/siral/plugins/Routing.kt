package com.siral.plugins

import com.siral.data.DataService
import com.siral.responses.Response
import com.siral.routes.*
import com.siral.security.token.TokenConfig
import com.siral.security.token.TokenService
import com.siral.utils.Messages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
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

        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, Response(success = false, data = cause.reasons.joinToString(), message = Messages.VALIDATION_ERROR))
        }

        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, Response(success = false, data = cause.toString(), message = Messages.MISSING_REQUIRED_FIELDS))
        }

    }

    routing {
        administration(dataService, tokenService, tokenConfig)
        dininghalls(dataService)
        logs(dataService)
        reservations(dataService)
        schedule(dataService)
        students(dataService, tokenConfig, tokenService)
    }
}

suspend inline fun ApplicationCall.withRole(roles: List<String>, block :() -> Unit){
    val principal = principal<JWTPrincipal>()
    val userRole = principal?.payload?.getClaim("userRole")?.asString()
    if (userRole in roles) block() else respond(HttpStatusCode.Forbidden, Response(success = false, data = null, message = Messages.ACCESS_DENIED))
}

