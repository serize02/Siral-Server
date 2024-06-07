package com.siral.routes

import com.siral.data.UserService
import com.siral.data.schedule.ScheduleItem
import com.siral.request.ScheduleItemRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

internal fun ScheduleItemRequest.toScheduleItem() = ScheduleItem(
    date = this.date,
    time = this.time,
    dinningHall = this.dinningHall,
)

fun Route.insertScheduleItem(userService: UserService) {
    authenticate {
        post("/siral/schedule") {
            val principal = call.principal<JWTPrincipal>()
            val role = principal?.getClaim("userRole", String::class)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Access denied role")
            if(role != "ADMIN")
                return@post call.respond(HttpStatusCode.Unauthorized, "You can't access this route")
            val adminId = principal.getClaim("userId", String::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError)
            val admin = userService.getAdminById(adminId)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val request = call.receive<List<ScheduleItemRequest>>()
            val schedule = request.map {it.toScheduleItem()}
            schedule.forEach {
                val dinninghall = userService.getDinningHallByName(it.dinningHall)
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "This Dinning Hall Doesn't Exist")
                val scheduleItem = userService.getScheduleItem(it)
                    ?: userService.insertScheduleItem(it)
            }
            return@post call.respond(HttpStatusCode.OK, "Schedule Updated Successfully")
        }
    }
}

@Suppress("IMPLICIT_CAST_TO_ANY")
fun Route.getScheduleForNextDays(userService: UserService) {
    authenticate {
        get("/siral/schedule/{dinningHallName}") {
            val principal = call.principal<JWTPrincipal>()
            // it can be an admin or a student
            val userId = principal?.getClaim("userId", String::class)
                ?: return@get call.respond(HttpStatusCode.InternalServerError)
            val role = principal.getClaim("userRole", String::class)
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val user = if(role == "ADMIN") userService.getAdminById(userId) else userService.getStudentById(userId)
                ?: return@get call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val dinningHallName = call.parameters["dinningHallName"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "DinningHall Name is required")
            val scheduleItems = userService.getScheduleForTheNextDays(dinningHallName)
            return@get call.respond(HttpStatusCode.OK, scheduleItems)
        }
    }
}