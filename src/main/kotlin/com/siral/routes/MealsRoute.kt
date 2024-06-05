package com.siral.routes

import com.siral.data.UserService
import com.siral.data.meal.Meal
import com.siral.request.MealRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

internal fun MealRequest.toMeal() = Meal(
    date = this.date,
    time = this.time,
    dinningHall = this.dinningHall,
)

/**
 * Insert Meals to the database:
 *
 * Only a user with an ADMIN role will have access to this endpoint,
 * (remember to add the role config for the server, right now it is only
 * an auxiliary route).
 *
 * As a request the server receive a List<MealRequest> we map this list
 * as a List<Meal> in order to generate the id for each meal and to storage
 * them in the database.
 */

fun Route.insertMeals(userService: UserService) {
    authenticate {
        post("/siral/meals") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError)
            val role = principal.getClaim("userRole", String::class)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Access denied role")
            if(role == "ADMIN")
                return@post call.respond(HttpStatusCode.Unauthorized, "You can't access this route")
            val user = userService.getUserById(userId)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val request = call.receive<List<MealRequest>>()
            val meals = request.map {it.toMeal()}
            meals.forEach {
                val meal = userService.getMeal(it)
                    ?: userService.insertMeals(it)
            }
            return@post call.respond(HttpStatusCode.OK, "Meals Added Successfully")
        }
    }
}

/**
 * Get Meals For The Next Days:
 *
 * You need to have a token to access to this route (be logged).
 * From the token the server can get the userId and if this value is equal to null
 * it will be sent an InternalServerError response. Then from the database
 * we get the user and if it is null it will be sent a NotFound response.
 *
 * We get the meals from the database that matches with the user's dinningHall and those that
 * has a posterior date from today.
 */

fun Route.getMealsForNextDays(userService: UserService) {
    authenticate {
        get("/siral/meals") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
                ?: return@get call.respond(HttpStatusCode.InternalServerError)
            val user = userService.getUserById(userId)
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val meals = userService.getMealsForTheNextDays(user)
            return@get call.respond(HttpStatusCode.OK, meals)
        }
    }
}

/**
 * Get Meal From Database By ID:
 *
 * You need to have a token to access to this route (be logged).
 * This route has a parameter (call.parameters["id"]) that represents the id
 * of one of the meals stored in the database. If the id is equals to null the
 * response will be a BadRequest with the message "Missing Meal ID". If the id does
 * not match with any meal stored in database it will be sent a NotFound response with
 * the message "This Meal Does Not Exist". In the case that it is found we respond with
 * Found and the meal information.
  */
fun Route.getMealById(
    userService: UserService
){
    authenticate {
        get("/siral/meals/{id}") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
                ?: return@get call.respond(HttpStatusCode.InternalServerError)
            val user = userService.getUserById(userId)
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val mealId = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing Meal Id")
            val meal = userService.getMealById(mealId)
                ?: return@get call.respond(HttpStatusCode.NotFound, "This Meal Does Not Exist")
            return@get call.respond(HttpStatusCode.Found, meal)
        }
    }
}