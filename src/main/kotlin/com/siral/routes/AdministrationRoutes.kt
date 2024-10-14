package com.siral.routes

import com.siral.data.DataService
import com.siral.request.AuthCredentials
import com.siral.request.CreateSiteManagerScheduler
import com.siral.responses.AuthResponse
import com.siral.responses.Response
import com.siral.security.token.TokenClaim
import com.siral.security.token.TokenConfig
import com.siral.security.token.TokenService
import com.siral.utils.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.call
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Routing.administration(dataService: DataService, tokenService: TokenService, tokenConfig: TokenConfig
){
    route("/administration"){
        get {
//            call.withRole(Access.administration){
                val personal = dataService.siteManagerSchedulerService.getAll()
                return@get call.respond(HttpStatusCode.OK, Response(data = personal, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
//            }
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLong()
            val user = id?.let { dataService.siteManagerSchedulerService.getByID(id) }
                ?: return@get call.respond(HttpStatusCode.NotFound, Response(success = false, data = null, message = Messages.USER_NOT_FOUND))
            return@get call.respond(HttpStatusCode.OK, Response(data = user, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
        }

        post {
//            call.withRole(Access.admin){
                val data = call.receive<CreateSiteManagerScheduler>()
                dataService.siteManagerSchedulerService.create(data)
                val new = dataService.siteManagerSchedulerService.getByEmail(data.email)
                return@post call.respond(HttpStatusCode.Created, Response(data = new, message = Messages.USER_CREATED_SUCCESSFULLY))
//            }
        }

        post("/login") {
            val credentials = call.receive<AuthCredentials>()

            if (!Admin.verify(credentials)) {
                val user = dataService.siteManagerSchedulerService.getByEmail(credentials.email)
                    ?: return@post call.respond(
                        HttpStatusCode.Unauthorized,
                        AuthResponse(success = false, data = null, message = Messages.INVALID_CREDENTIALS, role = null, token = null))

                val token = tokenService.generateToken(
                    config = tokenConfig,
                    claims = arrayOf(
                        TokenClaim(
                            name = "userId",
                            value = user.id.toString()
                        ),
                        TokenClaim(
                            name = "userRole",
                            value = user.role
                        )
                    )
                )

                dataService.logsService.create(credentials.email, Actions.LOGIN, Status.SUCCESSFUL)

                return@post call.respond(
                    HttpStatusCode.OK,
                    AuthResponse(data = user, message = Messages.USER_LOGGED_SUCCESSFULLY, role = null, token = token))
            }

            val token = tokenService.generateToken(
                config = tokenConfig,
                claims = arrayOf(
                    TokenClaim(
                        name = "userRole",
                        value = UserRole.ADMIN.name
                    )
                )
            )

            return@post call.respond(
                HttpStatusCode.OK,
                AuthResponse(data = null, message = Messages.USER_LOGGED_SUCCESSFULLY, role = null, token = token))
        }

        delete("/{id}") {
//            call.withRole(Access.admin){
                val id = call.parameters["id"]?.toLong()
                id?.let { dataService.siteManagerSchedulerService.getByID(id) }
                    ?: return@delete call.respond(HttpStatusCode.NotFound, Response(success = false, data = null, message = Messages.USER_NOT_FOUND))
                dataService.siteManagerSchedulerService.delete(id)
                return@delete call.respond(HttpStatusCode.OK, Response(data = null, message = Messages.USER_DELETED_SUCCESSFULLY))
//            }
        }

        get("/stats") {
            val data = dataService.getStatsData()
            return@get call.respond(HttpStatusCode.OK, Response(data = data, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
        }
    }
}