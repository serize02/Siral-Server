package com.siral.routes

import com.siral.data.UserService
import com.siral.request.NewRoleCredentials
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.insertNewRole(userService: UserService) {
    authenticate {
        post("siral/insert-new-role") {

            val principal = call.principal<JWTPrincipal>()
                ?: return@post call.respond(HttpStatusCode.InternalServerError)
            val role = principal.getClaim(name = "userRole", String::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError)
            if(role != "ADMIN")
                return@post call.respond(HttpStatusCode.Unauthorized, "Access Denied")
            val credentials = call.receive<NewRoleCredentials>()

            if(credentials.email.isEmpty() || credentials.dinninghall.isEmpty() || credentials.role.isEmpty())
                return@post call.respond(HttpStatusCode.BadRequest, "Missing Required Fields")

            if(!userService.verifyExistentDinninghall(credentials.dinninghall))
                return@post call.respond(HttpStatusCode.BadRequest, "Invalid Credentials")

            if(credentials.role != "SITE_MANAGER" && credentials.role != "SCHEDULER")
                return@post call.respond(HttpStatusCode.BadRequest, "Invalid Credentials")

            if (userService.verifyExistentEmail(credentials.email))
                return@post call.respond(HttpStatusCode.BadRequest, "This User Has Already a Role Associated")

            userService.insertNewSiteManagerScheduler(credentials)

            return@post call.respond(HttpStatusCode.OK, "User Inserted Successfully")
        }
    }
}

fun Route.deleteRole(userService: UserService) {
    authenticate {
        delete("siral/delete-role/{email}") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@delete call.respond(HttpStatusCode.InternalServerError)
            val role = principal.getClaim(name = "userRole", String::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError)
            if(role != "ADMIN")
                return@delete call.respond(HttpStatusCode.Unauthorized, "Access Denied")

            val email = call.parameters["email"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing Email")

            if(!userService.verifyExistentEmail(email))
                return@delete call.respond(HttpStatusCode.BadRequest, "User Not Found")

            userService.deleteSiteManagerScheduler(email)

            return@delete call.respond(HttpStatusCode.OK, "User Deleted Successfully")
        }
    }
}