package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.request.NewRoleCredentials
import com.siral.responses.Response
import com.siral.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.insertDinningHalls(dataService: DataService) {
    post("siral/dinninghalls/{dinninghallNAME}") {
        call.withRole(UserRole.ADMIN){
            val name = call.parameters["dinninghallNAME"]
                ?: run {
                    dataService.logsService.addLog(Admin.email, Actions.INSERT_DINNING_HALL, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.MISSING_DINNING_HALL_NAME.name, status = 404) )
                }

            val dinninghall = dataService.dinningHallService.getDinninghallByName(name)

            if(dinninghall != null) {
                dataService.logsService.addLog(Admin.email, Actions.INSERT_DINNING_HALL, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.DINNING_HALL_ALREADY_EXISTS.name, status = 404))
            }

            dataService.dinningHallService.insertDinninghall(name)
            dataService.logsService.addLog(Admin.email, Actions.INSERT_DINNING_HALL, Status.SUCCESSFUL)

            val newDinningHall = dataService.dinningHallService.getDinninghallByName(name)

            return@post call.respond(HttpStatusCode.OK, Response(success = true, data = newDinningHall, message = ResponseMessage.DINNING_HALL_INSERTED_SUCCESSFULLY.name, status = 200))
        }
    }
}

fun Route.deleteDinningHall(dataService: DataService) {
    delete("siral/dinninghalls/{dinninghallID}") {
        call.withRole(UserRole.ADMIN){
            val id = call.parameters["dinninghallID"]?.toLong()
                ?: run {
                    dataService.logsService.addLog(Admin.email, Actions.DELETE_DINNING_HALL, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.MISSING_DINNING_HALL_ID.name, status = 404))
                }

            dataService.dinningHallService.getDinninghallByID(id)
                ?: run {
                    dataService.logsService.addLog(Admin.email, Actions.DELETE_DINNING_HALL, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.DINNING_HALL_NOT_FOUND.name, status = 404))
                }

            dataService.dinningHallService.deleteDinninghall(id)
            dataService.logsService.addLog(Admin.email, Actions.DELETE_DINNING_HALL, Status.SUCCESSFUL)

            return@delete call.respond(HttpStatusCode.OK, Response(success = true, data = null, message = ResponseMessage.DINNING_HALL_DELETED_SUCCESSFULLY.name, status = 200))
        }
    }
}

fun Route.insertNewRole(dataService: DataService) {
    post("siral/insert-new-role") {
        call.withRole(UserRole.ADMIN){
            val credentials = call.receive<NewRoleCredentials>()

            if(credentials.email.isEmpty() || credentials.dinninghall.isEmpty() || credentials.role.isEmpty()){
                dataService.logsService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.MISSING_REQUIRED_FIELDS.name, status = 404))
            }

            if(!dataService.verifyExistentDinninghall(credentials.dinninghall)){
                dataService.logsService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.DINNING_HALL_NOT_FOUND.name, status = 404))
            }

            if(credentials.role != UserRole.SCHEDULER.name && credentials.role != UserRole.SITE_MANAGER.name){
                dataService.logsService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.INVALID_ROLE.name, status = 404))
            }

            if (dataService.verifyExistentEmail(credentials.email)){
                dataService.logsService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.USER_ALREADY_HAS_ROLE.name, status = 404))
            }

            dataService.siteManagerSchedulerService.insertNewSiteManagerScheduler(credentials)
            dataService.logsService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.SUCCESSFUL)

            val newUser = dataService.siteManagerSchedulerService.getSiteManagerSchedulerByEmail(credentials.email)

            return@post call.respond(HttpStatusCode.OK, Response(success = true, data = newUser, message = ResponseMessage.USER_INSERTED_SUCCESSFULLY.name, status = 200))
        }
    }
}

fun Route.deleteRole(dataService: DataService) {
    delete("siral/delete-role/{email}") {
        call.withRole(UserRole.ADMIN){
            val email = call.parameters["email"]
                ?: run {
                    dataService.logsService.addLog(Admin.email, Actions.DELETE_ROLE, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.MISSING_EMAIL.name, status = 404))
                }

            if(!dataService.verifyExistentEmail(email)){
                dataService.logsService.addLog(Admin.email, Actions.DELETE_ROLE, Status.FAILED)
                return@delete call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.USER_NOT_FOUND.name, status = 404))
            }

            dataService.siteManagerSchedulerService.deleteSiteManagerScheduler(email)
            dataService.logsService.addLog(Admin.email, Actions.DELETE_ROLE, Status.SUCCESSFUL)

            return@delete call.respond(HttpStatusCode.OK, Response(success = true, data = null, message = ResponseMessage.USER_DELETED_SUCCESSFULLY.name, status = 200))
        }
    }
}