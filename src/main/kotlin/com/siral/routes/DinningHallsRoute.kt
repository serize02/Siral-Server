package com.siral.routes

import com.siral.data.UserService
import com.siral.utils.Actions
import com.siral.utils.Admin
import com.siral.utils.ResponseMessage
import com.siral.utils.Status
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.insertDinningHalls(userService: UserService) {
    authenticate {
        post("siral/dinninghalls/{dinninghallNAME}") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@post call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)

            val role = principal.getClaim(name = "userRole", String::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, ResponseMessage.ROLE_NOT_FOUND_IN_TOKEN)

            if(role != "ADMIN")
                return@post call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

            val name = call.parameters["dinninghallNAME"]
                ?: run {
                    userService.addLog(Admin.email, Actions.INSERT_DINNING_HALL, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_DINNING_HALL_NAME)
                }
            val dinninghall = userService.getDinninghallByName(name)

            if(dinninghall != null) {
                userService.addLog(Admin.email, Actions.INSERT_DINNING_HALL, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.DINNING_HALL_ALREADY_EXISTS)
            }

            userService.insertDinninghall(name)
            userService.addLog(Admin.email, Actions.INSERT_DINNING_HALL, Status.SUCCESSFUL)

            return@post call.respond(HttpStatusCode.Created, ResponseMessage.DINNING_HALL_INSERTED_SUCCESSFULLY)
        }
    }
}

fun Route.deleteDinningHall(userService: UserService) {
    authenticate {
        delete("siral/dinninghalls/{dinninghallID}") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)

            val role = principal.getClaim(name = "userRole", String::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, ResponseMessage.ROLE_NOT_FOUND_IN_TOKEN)

            if(role != "ADMIN")
                return@delete call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

            val id = call.parameters["dinninghallID"]?.toLong()
                ?: run {
                    userService.addLog(Admin.email, Actions.DELETE_DINNING_HALL, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_DINNING_HALL_NAME)
                }

            val dinninghall = userService.getDinninghallByID(id)
                ?: run {
                    userService.addLog(Admin.email, Actions.DELETE_DINNING_HALL, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.DINNING_HALL_NOT_FOUND)
                }

            userService.deleteDinninghall(id)
            userService.addLog(Admin.email, Actions.DELETE_DINNING_HALL, Status.SUCCESSFUL)

            return@delete call.respond(HttpStatusCode.OK, ResponseMessage.DINNING_HALL_DELETED_SUCCESSFULLY)
        }
    }
}

