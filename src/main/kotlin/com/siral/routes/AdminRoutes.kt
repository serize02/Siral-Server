package com.siral.routes

import com.siral.data.UserService
import com.siral.plugins.withRole
import com.siral.request.NewRoleCredentials
import com.siral.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.insertDinningHalls(userService: UserService) {
    post("siral/dinninghalls/{dinninghallNAME}") {
        call.withRole(UserRole.ADMIN){
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
    delete("siral/dinninghalls/{dinninghallID}") {
        call.withRole(UserRole.ADMIN){
            val id = call.parameters["dinninghallID"]?.toLong()
                ?: run {
                    userService.addLog(Admin.email, Actions.DELETE_DINNING_HALL, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_DINNING_HALL_NAME)
                }

            userService.getDinninghallByID(id)
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

fun Route.insertNewRole(userService: UserService) {
    post("siral/insert-new-role") {
        call.withRole(UserRole.ADMIN){
            val credentials = call.receive<NewRoleCredentials>()

            if(credentials.email.isEmpty() || credentials.dinninghall.isEmpty() || credentials.role.isEmpty()){
                userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_REQUIRED_FIELDS)
            }

            if(!userService.verifyExistentDinninghall(credentials.dinninghall)){
                userService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.DINNING_HALL_NOT_FOUND)
            }

            if(credentials.role != UserRole.SCHEDULER.name && credentials.role != UserRole.SITE_MANAGER.name){
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
    delete("siral/delete-role/{email}") {
        call.withRole(UserRole.ADMIN){
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