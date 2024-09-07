package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.utils.Actions
import com.siral.utils.ResponseMessage
import com.siral.utils.Status
import com.siral.utils.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.makeReservations(
    dataService: DataService
) {
    post("/siral/reservations/{studentID}/{scheduleItemID}") {
        call.withRole(UserRole.STUDENT){
            try {
                val studentID = call.parameters["studentID"]?.toLong()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_STUDENT_ID)

                val student = dataService.studentService.getStudentById(studentID)
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

                val scheduleItemId = call.parameters["scheduleItemID"]?.toLong()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_SCHEDULE_ITEM_ID)

                val foundReservation = dataService.reservationService.getReservationByScheduleItemIdAndUserId(scheduleItemId, studentID)
                if(foundReservation != null){
                    dataService.logsService.addLog(student.email, Actions.MAKE_RESERVATION, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MEAL_ALREADY_RESERVED)
                }

                // Check if the schedule item exists
                val scheduleItem = dataService.scheduleService.getScheduleItemById(scheduleItemId)
                    ?: run {
                        dataService.logsService.addLog(student.email, Actions.MAKE_RESERVATION, Status.FAILED)
                        return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.SCHEDULE_ITEM_NOT_FOUND)
                    }

                if(!scheduleItem.available){
                    dataService.logsService.addLog(student.email, Actions.MAKE_RESERVATION, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.SCHEDULE_ITEM_NOT_AVAILABLE)
                }

                val reservation = dataService.reservationService.getReservationByScheduleItemIdAndUserId(studentID, scheduleItemId)
                if(reservation != null){
                    dataService.logsService.addLog(student.email, Actions.MAKE_RESERVATION, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MEAL_ALREADY_RESERVED)
                }

                dataService.reservationService.makeReservation(studentID, scheduleItemId)
                dataService.logsService.addLog(student.email, Actions.MAKE_RESERVATION, Status.SUCCESSFUL)

                return@post call.respond(HttpStatusCode.OK, ResponseMessage.RESERVATION_MADE)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
            }
        }
    }
}

fun Route.deleteReservation(
    dataService: DataService
){
    delete("/siral/reservations/{studentID}/{reservationID}") {
        call.withRole(UserRole.STUDENT){
            try {
                val studentID = call.parameters["studentID"]?.toLong()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_STUDENT_ID)

                val student = dataService.studentService.getStudentById(studentID)
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

                val reservationId = call.parameters["reservationID"]?.toLong()
                    ?: run {
                        dataService.logsService.addLog(student.email, Actions.DELETE_RESERVATION, Status.FAILED)
                        return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_RESERVATION_ID)
                    }

                dataService.reservationService.getReservationByID(reservationId)
                    ?: run {
                        dataService.logsService.addLog(student.email, Actions.DELETE_RESERVATION, Status.FAILED)
                        return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.RESERVATION_NOT_FOUND)
                    }

                dataService.reservationService.deleteReservation(reservationId)
                dataService.logsService.addLog(student.email, Actions.DELETE_RESERVATION, Status.SUCCESSFUL)
                return@delete call.respond(HttpStatusCode.OK, ResponseMessage.RESERVATION_DELETED)
            } catch (e: Exception){
                call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
            }
        }
    }

}

fun Route.getStudentReservations(
    userService: DataService
){
    get("/siral/reservations/{studentID}") {
        call.withRole(UserRole.STUDENT){
            try {
                val studentID = call.parameters["studentID"]?.toLong()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_REQUIRED_FIELDS)
                val reservations = userService.reservationService.getReservations(studentID)
                return@get call.respond(HttpStatusCode.OK, reservations)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
            }
        }
    }
}

