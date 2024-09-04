package com.siral.routes

import com.siral.data.UserService
import com.siral.utils.Actions
import com.siral.utils.ResponseMessage
import com.siral.utils.Status
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.makeReservations(
    userService: UserService
) {
    authenticate {
        post("/siral/reservations/{scheduleItemID}") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@post call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)

            val userId = principal?.getClaim("userId", Long::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, ResponseMessage.ID_NOT_FOUND_IN_TOKEN)

            val role = principal.getClaim("userRole", String::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, ResponseMessage.ROLE_NOT_FOUND_IN_TOKEN)

            if(role != "STUDENT")
                return@post call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

                ?: return@post call.respond(HttpStatusCode.InternalServerError, "Role not found in token")
            val user = userService.getStudentById(userId)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

            val scheduleItemId = call.parameters["scheduleItemID"]?.toLong()
                ?: return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_SCHEDULE_ITEM_ID)

            val foundReservation = userService.getReservationByScheduleItemIdAndUserId(scheduleItemId, userId)
            if(foundReservation != null){
                userService.addLog(user.email, Actions.MAKE_RESERVATION, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MEAL_ALREADY_RESERVED)
            }

            // Check if the schedule item exists
            val scheduleItem = userService.getScheduleItemById(scheduleItemId)
                ?: run {
                    userService.addLog(user.email, Actions.MAKE_RESERVATION, Status.FAILED)
                    return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.SCHEDULE_ITEM_NOT_FOUND)
                }

            if(!scheduleItem.available){
                userService.addLog(user.email, Actions.MAKE_RESERVATION, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.SCHEDULE_ITEM_NOT_AVAILABLE)
            }

            userService.makeReservation(userId, scheduleItemId)
            userService.addLog(user.email, Actions.MAKE_RESERVATION, Status.SUCCESSFUL)

            return@post call.respond(HttpStatusCode.OK, ResponseMessage.RESERVATION_MADE)
        }
    }
}


fun Route.deleteReservation(
    userService: UserService
){
    authenticate {
        delete("/siral/reservations/{reservationID}") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
            val userId = principal?.getClaim("userId", Long::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, ResponseMessage.ID_NOT_FOUND_IN_TOKEN)

            val role = principal.getClaim("userRole", String::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, ResponseMessage.ROLE_NOT_FOUND_IN_TOKEN)

            if(role != "STUDENT")
                return@delete call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

            val user = userService.getStudentById(userId)
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

            val reservationId = call.parameters["reservationID"]?.toLong()
                ?: run {
                    userService.addLog(user.email, Actions.DELETE_RESERVATION, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_RESERVATION_ID)
                }
            userService.deleteReservation(reservationId)
            userService.addLog(user.email, Actions.DELETE_RESERVATION, Status.SUCCESSFUL)
            call.respond(HttpStatusCode.OK, ResponseMessage.RESERVATION_DELETED)
        }
    }
}


fun Route.getStudentReservations(
    userService: UserService
){
    authenticate {
        get("/siral/reservations") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@get call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
            val userId = principal.getClaim("userId", Long::class)
                ?: return@get call.respond(HttpStatusCode.InternalServerError, ResponseMessage.ID_NOT_FOUND_IN_TOKEN)

            val role = principal.getClaim("userRole", String::class)
                ?: return@get call.respond(HttpStatusCode.InternalServerError, ResponseMessage.ROLE_NOT_FOUND_IN_TOKEN)

            if(role != "STUDENT")
                return@get call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)

            val user = userService.getStudentById(userId)
                ?: return@get call.respond(HttpStatusCode.Unauthorized, ResponseMessage.ACCESS_DENIED)
            val reservations = userService.getReservations(userId)
            return@get call.respond(HttpStatusCode.OK, reservations)
        }
    }
}

