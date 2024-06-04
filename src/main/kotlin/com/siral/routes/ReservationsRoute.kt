package com.siral.routes

import com.siral.data.UserService
import com.siral.data.reservation.Reservation
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * You need to have a token to access to this route in order to
 * insert in the database a new reservation for a user. This implementation
 * will check if the user is registered in the database and if the meal for the
 * reservation exists and if it is active (it can be reserved).
 */
fun Route.insertReservations(
    userService: UserService
) {
    authenticate {
        post("/siral/reservations/{id}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError)
            val user = userService.getUserById(userId)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val mealId = call.parameters["id"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing Meal ID")
            val foundMeal = userService.getReservationByMealdId(mealId) //in reservations
            if(foundMeal == null) {
                val meal = userService.getMealById(mealId)
                    ?: return@post call.respond(HttpStatusCode.NotFound, "Meal not found")
                if(!meal.active)
                    return@post call.respond(HttpStatusCode.ServiceUnavailable, "This meal is not available")
                userService.insertReservation(
                    Reservation(
                        userId = userId,
                        mealId = mealId,
                    )
                )
                return@post call.respond(HttpStatusCode.Created, "Reservation Done")
            }
            return@post call.respond(HttpStatusCode.ServiceUnavailable, "You Already Reserved For This Meal")

        }
    }
}

/**
 * You need to have a token to access here. You need to include in the
 * parameters the id of the reservation that you want to delete. This implementation
 * will check if the id provided in the parameters is valid (id != null), if this is the
 * case then you will get as a response a BadRequest with the message ("Reservation ID
 * is required"). If that is not the case, then will be deleted.
 */
fun Route.deleteReservation(
    userService: UserService
){
    authenticate {
        delete("/siral/reservations/{id}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError)
            val user = userService.getUserById(userId)
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val reservationId = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing Reservation Id")
            userService.deleteReservation(reservationId)
            call.respond(HttpStatusCode.OK, "Reservation Deleted")
        }
    }
}

/**
 * You need to have a token to access here, this route will provide
 * a list of all the reservations that the authenticated user have registered
 * in the database
 */
fun Route.getReservations(
    userService: UserService
){
    authenticate {
        get("/siral/reservations") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
                ?: return@get call.respond(HttpStatusCode.InternalServerError)
            val user = userService.getUserById(userId)
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val reservations = userService.getReservations(userId)
            return@get call.respond(HttpStatusCode.OK, reservations)
        }
    }
}

