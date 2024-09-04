package com.siral.routes

import com.siral.data.UserService
import com.siral.request.NewRoleCredentials
import com.siral.utils.Actions
import com.siral.utils.Admin
import com.siral.utils.ResponseMessage
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
                ?: return@post call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)

            val role = principal.getClaim(name = "userRole", String::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, ResponseMessage.ROLE_NOT_FOUND_IN_TOKEN)

            if(role != "ADMIN")
                return@post call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

            val credentials = call.receive<NewRoleCredentials>()

            if(credentials.email.isEmpty() || credentials.dinninghall.isEmpty() || credentials.role.isEmpty()){
                userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_REQUIRED_FIELDS)
            }

            if(!userService.verifyExistentDinninghall(credentials.dinninghall)){
                userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.DINNING_HALL_NOT_FOUND)
            }

            if(credentials.role != "SITE_MANAGER" && credentials.role != "SCHEDULER"){
                userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.INVALID_ROLE)
            }

            if (userService.verifyExistentEmail(credentials.email)){
                userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.USER_ALREADY_HAS_ROLE)
            }

            userService.insertNewSiteManagerScheduler(credentials)
            userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.SUCCESSFUL)

            return@post call.respond(HttpStatusCode.OK, ResponseMessage.USER_INSERTED_SUCCESSFULLY)
        }
    }
}

fun Route.deleteRole(userService: UserService) {
    authenticate {
        delete("siral/delete-role/{email}") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)

            val role = principal.getClaim(name = "userRole", String::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, ResponseMessage.ROLE_NOT_FOUND_IN_TOKEN)

            if(role != "ADMIN")
                return@delete call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

            val email = call.parameters["email"]
                ?: run {
                    userService.addLog(Admin.email, Actions.DELETE_ROLE, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_EMAIL)
                }

            if(!userService.verifyExistentEmail(email)){
                userService.addLog(Admin.email, Actions.DELETE_ROLE, Status.FAILED)
                return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.USER_NOT_FOUND)
            }

            userService.deleteSiteManagerScheduler(email)
            userService.addLog(Admin.email, Actions.DELETE_ROLE, Status.SUCCESSFUL)

            return@delete call.respond(HttpStatusCode.OK, ResponseMessage.USER_DELETED_SUCCESSFULLY)
        }
    }
}