package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.request.NewRoleCredentials
import com.siral.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.insertDinningHalls(dataService: DataService) {
    post("siral/dinninghalls/{dinninghallNAME}") {
        call.withRole(UserRole.ADMIN){
            try{
                val name = call.parameters["dinninghallNAME"]
                    ?: run {
                        dataService.logsService.addLog(Admin.email, Actions.INSERT_DINNING_HALL, Status.FAILED)
                        return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_DINNING_HALL_NAME)
                    }

                val dinninghall = dataService.dinningHallService.getDinninghallByName(name)

                if(dinninghall != null) {
                    dataService.logsService.addLog(Admin.email, Actions.INSERT_DINNING_HALL, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.DINNING_HALL_ALREADY_EXISTS)
                }

                dataService.dinningHallService.insertDinninghall(name)
                dataService.logsService.addLog(Admin.email, Actions.INSERT_DINNING_HALL, Status.SUCCESSFUL)

                return@post call.respond(HttpStatusCode.Created, ResponseMessage.DINNING_HALL_INSERTED_SUCCESSFULLY)

            } catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
            }
        }
    }
}

fun Route.deleteDinningHall(dataService: DataService) {
    delete("siral/dinninghalls/{dinninghallID}") {
        call.withRole(UserRole.ADMIN){
            try {
                val id = call.parameters["dinninghallID"]?.toLong()
                    ?: run {
                        dataService.logsService.addLog(Admin.email, Actions.DELETE_DINNING_HALL, Status.FAILED)
                        return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_DINNING_HALL_NAME)
                    }

                dataService.dinningHallService.getDinninghallByID(id)
                    ?: run {
                        dataService.logsService.addLog(Admin.email, Actions.DELETE_DINNING_HALL, Status.FAILED)
                        return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.DINNING_HALL_NOT_FOUND)
                    }

                dataService.dinningHallService.deleteDinninghall(id)
                dataService.logsService.addLog(Admin.email, Actions.DELETE_DINNING_HALL, Status.SUCCESSFUL)

                return@delete call.respond(HttpStatusCode.OK, ResponseMessage.DINNING_HALL_DELETED_SUCCESSFULLY)

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
            }
        }
    }
}

fun Route.insertNewRole(dataService: DataService) {
    post("siral/insert-new-role") {
        call.withRole(UserRole.ADMIN){
            try {
                val credentials = call.receive<NewRoleCredentials>()

                if(credentials.email.isEmpty() || credentials.dinninghall.isEmpty() || credentials.role.isEmpty()){
                    dataService.logsService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_REQUIRED_FIELDS)
                }

                if(!dataService.verifyExistentDinninghall(credentials.dinninghall)){
                    dataService.logsService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.DINNING_HALL_NOT_FOUND)
                }

                if(credentials.role != UserRole.SCHEDULER.name && credentials.role != UserRole.SITE_MANAGER.name){
                    dataService.logsService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.INVALID_ROLE)
                }

                if (dataService.verifyExistentEmail(credentials.email)){
                    dataService.logsService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.USER_ALREADY_HAS_ROLE)
                }

                dataService.siteManagerSchedulerService.insertNewSiteManagerScheduler(credentials)
                dataService.logsService.addLog(Admin.email, Actions.INSERT_NEW_ROLE, Status.SUCCESSFUL)

                return@post call.respond(HttpStatusCode.OK, ResponseMessage.USER_INSERTED_SUCCESSFULLY)

            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
            }
        }
    }
}

fun Route.deleteRole(dataService: DataService) {
    delete("siral/delete-role/{email}") {
        call.withRole(UserRole.ADMIN){
            try {
                val email = call.parameters["email"]
                    ?: run {
                        dataService.logsService.addLog(Admin.email, Actions.DELETE_ROLE, Status.FAILED)
                        return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_EMAIL)
                    }

                if(!dataService.verifyExistentEmail(email)){
                    dataService.logsService.addLog(Admin.email, Actions.DELETE_ROLE, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.USER_NOT_FOUND)
                }

                dataService.siteManagerSchedulerService.deleteSiteManagerScheduler(email)
                dataService.logsService.addLog(Admin.email, Actions.DELETE_ROLE, Status.SUCCESSFUL)

                return@delete call.respond(HttpStatusCode.OK, ResponseMessage.USER_DELETED_SUCCESSFULLY)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
            }
        }
    }
}