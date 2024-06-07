package com.siral.routes

import com.siral.data.UserService
import com.siral.data.reservation.Reservation
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.insertReservations(
    userService: UserService
) {
    authenticate {
        post("/siral/reservations/{id}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError)
            val user = userService.getStudentById(userId)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val scheduleItemId = call.parameters["id"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing Schedule Item ID")
            val foundScheduleItem = userService.getReservationByMealIdAndUserId(scheduleItemId, userId) //in reservations
            if(foundScheduleItem == null) {
                val scheduleItem = userService.getScheduleItemById(scheduleItemId)
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Meal not found")
                if(!scheduleItem.active || scheduleItem.dinningHall != user.dinningHall)
                    return@post call.respond(HttpStatusCode.BadRequest, "This Schedule Item Is Not Available")
                userService.insertReservation(
                    Reservation(
                        userId = userId,
                        scheduleItemId = scheduleItemId,
                    )
                )
                return@post call.respond(HttpStatusCode.Created, "Reservation Done")
            }
            return@post call.respond(HttpStatusCode.BadRequest, "You Already Reserved For This Schedule Item")
        }
    }
}


fun Route.deleteReservation(
    userService: UserService
){
    authenticate {
        delete("/siral/reservations/{id}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError)
            val user = userService.getStudentById(userId)
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val reservationId = call.parameters["id"]
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
            val role = principal?.getClaim("userRole", String::class)
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Access denied role")
            if(role != "STUDENT")
                return@get call.respond(HttpStatusCode.Unauthorized, "You can't access this route")
            val userId = principal.getClaim("userId", String::class)
                ?: return@get call.respond(HttpStatusCode.InternalServerError)
            val user = userService.getStudentById(userId)
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val reservations = userService.getReservations(userId)
            return@get call.respond(HttpStatusCode.OK, reservations)
        }
    }
}

