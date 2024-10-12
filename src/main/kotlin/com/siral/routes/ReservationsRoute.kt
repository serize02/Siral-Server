package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.responses.Response
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
            val studentID = call.parameters["studentID"]?.toLong()
                ?: return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.MISSING_STUDENT_ID.name, status = 404))

            val student = dataService.studentService.getStudentById(studentID)
                ?: return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.INVALID_STUDENT.name, status = 404))

            val scheduleItemId = call.parameters["scheduleItemID"]?.toLong()
                ?: return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.MISSING_SCHEDULE_ITEM_ID.name, status = 404))

            val foundReservation = dataService.reservationService.getReservationByScheduleItemIdAndUserId(scheduleItemId, studentID)
            if(foundReservation != null){
                dataService.logsService.addLog(student.email, Actions.MAKE_RESERVATION, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.MEAL_ALREADY_RESERVED.name, status = 404))
            }

            // Check if the schedule item exists
            val scheduleItem = dataService.scheduleService.getScheduleItemById(scheduleItemId)
                ?: run {
                    dataService.logsService.addLog(student.email, Actions.MAKE_RESERVATION, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.SCHEDULE_ITEM_NOT_FOUND.name, status = 404))
                }

            if(!scheduleItem.available){
                dataService.logsService.addLog(student.email, Actions.MAKE_RESERVATION, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.SCHEDULE_ITEM_NOT_AVAILABLE.name, status = 404))
            }

            var reservation = dataService.reservationService.getReservationByScheduleItemIdAndUserId(studentID, scheduleItemId)
            if(reservation != null){
                dataService.logsService.addLog(student.email, Actions.MAKE_RESERVATION, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.MEAL_ALREADY_RESERVED.name, status = 404))
            }

            dataService.reservationService.makeReservation(studentID, scheduleItemId)
            dataService.logsService.addLog(student.email, Actions.MAKE_RESERVATION, Status.SUCCESSFUL)

            val newReservation = dataService.reservationService.getReservationByScheduleItemIdAndUserId(studentID, scheduleItemId)

            return@post call.respond(HttpStatusCode.OK, Response(success = true, data = newReservation, message = ResponseMessage.RESERVATION_MADE.name, status = 200))
        }
    }
}

fun Route.deleteReservation(
    dataService: DataService
){
    delete("/siral/reservations/{studentID}/{reservationID}") {
        call.withRole(UserRole.STUDENT){
            val studentID = call.parameters["studentID"]?.toLong()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.MISSING_STUDENT_ID.name, status = 404))

            val student = dataService.studentService.getStudentById(studentID)
                ?: return@delete call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.INVALID_STUDENT.name, status = 404))

            val reservationId = call.parameters["reservationID"]?.toLong()
                ?: run {
                    dataService.logsService.addLog(student.email, Actions.DELETE_RESERVATION, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.MISSING_RESERVATION_ID.name, status = 404))
                }

            dataService.reservationService.getReservationByID(reservationId)
                ?: run {
                    dataService.logsService.addLog(student.email, Actions.DELETE_RESERVATION, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.RESERVATION_NOT_FOUND.name, status = 404))
                }

            dataService.reservationService.deleteReservation(reservationId)
            dataService.logsService.addLog(student.email, Actions.DELETE_RESERVATION, Status.SUCCESSFUL)
            return@delete call.respond(HttpStatusCode.OK, Response(success = true, data = null, message = ResponseMessage.RESERVATION_DELETED.name, status = 200))
        }
    }

}

fun Route.getStudentReservations(
    userService: DataService
){
    get("/siral/reservations/{studentID}") {
        call.withRole(UserRole.STUDENT){
            val studentID = call.parameters["studentID"]?.toLong()
                ?: return@get call.respond(HttpStatusCode.BadRequest, Response(success = false, data = null, message = ResponseMessage.MISSING_STUDENT_ID.name, status = 404))
            val reservations = userService.reservationService.getReservations(studentID)
            return@get call.respond(HttpStatusCode.OK, Response(success = true, data = reservations, message = ResponseMessage.DATA_RETREIVED_SUCCESSFULLY.name, status = 200))
        }
    }
}

