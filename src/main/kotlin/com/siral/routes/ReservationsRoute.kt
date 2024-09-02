package com.siral.routes

import com.siral.data.UserService
import com.siral.data.reservation.Reservation
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

            val userId = principal?.getClaim("userId", Long::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, "User ID not found in token")

            val role = principal.getClaim("userRole", String::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, "Role not found in token")

            if(role != "STUDENT")
                return@post call.respond(HttpStatusCode.Unauthorized, "Access Denied")

                ?: return@post call.respond(HttpStatusCode.InternalServerError, "Role not found in token")
            val user = userService.getStudentById(userId)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Access denied")

            val scheduleItemId = call.parameters["scheduleItemID"]?.toLong()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing Schedule Item ID")

            // Check if the user has already reserved this meal
            val foundScheduleItem = userService.getReservationByScheduleItemIdAndUserId(scheduleItemId, userId)
            if(foundScheduleItem != null)
                return@post call.respond(HttpStatusCode.BadRequest, "You have already reserved this meal")

            // Check if the schedule item exists
            val scheduleItem = userService.getScheduleItemById(scheduleItemId)
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Schedule Item not found")

            if(!scheduleItem.available)
                return@post call.respond(HttpStatusCode.BadRequest, "Schedule Item not available")

            userService.makeReservation(userId, scheduleItemId)

            return@post call.respond(HttpStatusCode.OK, "Reservation made")
        }
    }
}


fun Route.deleteReservation(
    userService: UserService
){
    authenticate {
        delete("/siral/reservations/{reservationID}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, "ID not found in token")

            val role = principal.getClaim("userRole", String::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, "Role not found in token")

            if(role != "STUDENT")
                return@delete call.respond(HttpStatusCode.Unauthorized, "Access Denied")

            val user = userService.getStudentById(userId)
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Access denied")

            val reservationId = call.parameters["reservationID"]?.toLong()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing Reservation Id")

            userService.deleteReservation(reservationId)
            call.respond(HttpStatusCode.OK, "Reservation Deleted")
        }
    }
}


fun Route.getStudentReservations(
    userService: UserService
){
    authenticate {
        get("/siral/reservations") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)
                ?: return@get call.respond(HttpStatusCode.InternalServerError, "ID not found in token")

            val role = principal.getClaim("userRole", String::class)
                ?: return@get call.respond(HttpStatusCode.InternalServerError, "Role not found in token")

            if(role != "STUDENT")
                return@get call.respond(HttpStatusCode.Unauthorized, "Access Denied")

            val user = userService.getStudentById(userId)
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val reservations = userService.getReservations(userId)
            return@get call.respond(HttpStatusCode.OK, reservations)
        }
    }
}

