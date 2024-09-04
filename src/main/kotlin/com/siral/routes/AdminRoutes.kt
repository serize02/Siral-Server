package com.siral.routes

import com.siral.data.UserService
import com.siral.request.NewRoleCredentials
import com.siral.utils.Actions
import com.siral.utils.Admin
import com.siral.utils.Status
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

            if(credentials.email.isEmpty() || credentials.dinninghall.isEmpty() || credentials.role.isEmpty()){
                userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, "Missing Required Fields")
            }

            if(!userService.verifyExistentDinninghall(credentials.dinninghall)){
                userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, "Invalid Credentials")
            }

            if(credentials.role != "SITE_MANAGER" && credentials.role != "SCHEDULER"){
                userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, "Invalid Credentials")
            }

            if (userService.verifyExistentEmail(credentials.email)){
                userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, "This User Has Already a Role Associated")
            }

            userService.insertNewSiteManagerScheduler(credentials)
            userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.SUCCESSFUL)

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
                ?: run {
                    userService.addLog(Admin.email, Actions.DELETE_ROLE, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, "Missing Email")
                }

            if(!userService.verifyExistentEmail(email)){
                userService.addLog(Admin.email, Actions.DELETE_ROLE, Status.FAILED)
                return@delete call.respond(HttpStatusCode.BadRequest, "User Not Found")
            }


            userService.deleteSiteManagerScheduler(email)
            userService.addLog(Admin.email, Actions.DELETE_ROLE, Status.SUCCESSFUL)

            return@delete call.respond(HttpStatusCode.OK, "User Deleted Successfully")
        }
    }
}