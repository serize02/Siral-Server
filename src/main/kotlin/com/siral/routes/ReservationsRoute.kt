package com.siral.routes

import com.siral.data.UserService
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
    userService: UserService
) {
    post("/siral/reservations/{studentID}/{scheduleItemID}") {
        call.withRole(UserRole.STUDENT){
            val studentID = call.parameters["studentID"]?.toLong()
                ?: return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_STUDENT_ID)

            val student = userService.getStudentById(studentID)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

            val scheduleItemId = call.parameters["scheduleItemID"]?.toLong()
                ?: return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_SCHEDULE_ITEM_ID)

            val foundReservation = userService.getReservationByScheduleItemIdAndUserId(scheduleItemId, studentID)
            if(foundReservation != null){
                userService.addLog(student.email, Actions.MAKE_RESERVATION, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MEAL_ALREADY_RESERVED)
            }

            // Check if the schedule item exists
            val scheduleItem = userService.getScheduleItemById(scheduleItemId)
                ?: run {
                    userService.addLog(student.email, Actions.MAKE_RESERVATION, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.SCHEDULE_ITEM_NOT_FOUND)
                }

            if(!scheduleItem.available){
                userService.addLog(student.email, Actions.MAKE_RESERVATION, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.SCHEDULE_ITEM_NOT_AVAILABLE)
            }

            userService.makeReservation(studentID, scheduleItemId)
            userService.addLog(student.email, Actions.MAKE_RESERVATION, Status.SUCCESSFUL)

            return@post call.respond(HttpStatusCode.OK, ResponseMessage.RESERVATION_MADE)

        }
    }

}


fun Route.deleteReservation(
    userService: UserService
){
    delete("/siral/reservations/{studentID}/{reservationID}") {
        call.withRole(UserRole.STUDENT){
            val studentID = call.parameters["studentID"]?.toLong()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_STUDENT_ID)

            val student = userService.getStudentById(studentID)
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

            val reservationId = call.parameters["reservationID"]?.toLong()
                ?: run {
                    userService.addLog(student.email, Actions.DELETE_RESERVATION, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_RESERVATION_ID)
                }

            userService.getReservationByID(reservationId)
                ?: run {
                    userService.addLog(student.email, Actions.DELETE_RESERVATION, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.RESERVATION_NOT_FOUND)
                }

            userService.deleteReservation(reservationId)
            userService.addLog(student.email, Actions.DELETE_RESERVATION, Status.SUCCESSFUL)
            return@delete call.respond(HttpStatusCode.OK, ResponseMessage.RESERVATION_DELETED)
        }
    }

}


fun Route.getStudentReservations(
    userService: UserService
){
    get("/siral/reservations/{studentID}") {
        call.withRole(UserRole.STUDENT){
            val studentID = call.parameters["studentID"]?.toLong()
                ?: return@get call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_REQUIRED_FIELDS)
            val reservations = userService.getReservations(studentID)
            return@get call.respond(HttpStatusCode.OK, reservations)
        }
    }
}

