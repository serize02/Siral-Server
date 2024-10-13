package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.responses.Response
import com.siral.utils.ResponseMessage
import com.siral.utils.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getStatsData(dataService: DataService) {
    get("siral/stats-data") {
        call.withRole(listOf(UserRole.ADMIN.name, UserRole.SCHEDULER.name, UserRole.SITE_MANAGER.name)){
            val data = dataService.getStatsData()
            return@get call.respond(HttpStatusCode.OK, Response(success = true, data = data, message = ResponseMessage.DATA_RETRIEVED_SUCCESSFULLY.name, status = 200))
        }
    }
}

fun Route.getAllLogs(dataService: DataService){
    get("siral/logs"){
        call.withRole(listOf(UserRole.ADMIN.name, UserRole.SITE_MANAGER.name, UserRole.SCHEDULER.name)){
            val logs = dataService.logsService.getLogs()
            return@get call.respond(HttpStatusCode.OK, Response(success = true, data = logs, message = ResponseMessage.DATA_RETRIEVED_SUCCESSFULLY.name, status = 200))
        }
    }
}

fun Route.getAdministrationPersonal(dataService: DataService){
    get("siral/administration") {
        call.withRole(listOf(UserRole.ADMIN.name, UserRole.SCHEDULER.name, UserRole.SITE_MANAGER.name)){
            val administration = dataService.siteManagerSchedulerService.getAll()
            return@get call.respond(HttpStatusCode.OK, Response(success = true, data = administration, message = ResponseMessage.DATA_RETRIEVED_SUCCESSFULLY.name, status = 200))
        }
    }
}

fun Route.getAllStudents(dataService: DataService){
    get("siral/students") {
        call.withRole(listOf(UserRole.ADMIN.name, UserRole.SCHEDULER.name, UserRole.SITE_MANAGER.name)){
            val students = dataService.studentService.getAll()
            return@get call.respond(HttpStatusCode.OK, Response(success = true, data = students, message = ResponseMessage.DATA_RETRIEVED_SUCCESSFULLY.name, status = 200))
        }
    }
}

fun Route.getAllDinningHalls(dataService: DataService){
    get("siral/dinninghalls") {
        call.withRole(listOf(UserRole.ADMIN.name, UserRole.SCHEDULER.name, UserRole.SITE_MANAGER.name)){
            val dinninghalls = dataService.dinningHallService.getAll()
            return@get call.respond(HttpStatusCode.OK, Response(success = true, data = dinninghalls, message = ResponseMessage.DATA_RETRIEVED_SUCCESSFULLY.name, status = 200))
        }
    }
}

fun Route.getAllReservations(dataService: DataService){
    get("siral/reservations") {
        call.withRole(listOf(UserRole.ADMIN.name, UserRole.SCHEDULER.name, UserRole.SITE_MANAGER.name)){
            val reservations = dataService.reservationService.getAll()
            return@get call.respond(HttpStatusCode.OK, Response(success = true, data = reservations, message = ResponseMessage.DATA_RETRIEVED_SUCCESSFULLY.name, status = 200))
        }
    }
}

