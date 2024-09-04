package com.siral.routes

import com.siral.data.UserService
import com.siral.request.ScheduleItemRequest
import com.siral.utils.Actions
import com.siral.utils.Status
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate


fun Route.getSchedule(userService: UserService) {
    get("siral/schedule/{dinninghallID}") {
        val dinninghallID = call.parameters["dinninghallID"]?.toLong()
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing Dinning Hall ID")
        val dinninghall = userService.getDinninghallByID(dinninghallID)
            ?: return@get call.respond(HttpStatusCode.NotFound, "This Dinning Hall Doest Not Exist")
        val items = userService.getSchedule(dinninghallID)
        return@get call.respond(HttpStatusCode.OK, items)
    }
}


fun Route.insertScheduleItem(userService: UserService){
    authenticate {
        post("siral/schedule") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@post call.respond(HttpStatusCode.InternalServerError)

            val role = principal.getClaim(name = "userRole", String::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError)

            if(role != "SCHEDULER")
                return@post call.respond(HttpStatusCode.Unauthorized, "Access Denied")

            val schedulerId = principal.getClaim(name = "userId", Long::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, "Scheduler ID is null")

            val scheduler = userService.getSiteManagerSchedulerByID(schedulerId)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, "Scheduler not found")

            val dinninghall = userService.getDinninghallByID(scheduler.dinninghallID)
                ?: run {
                    userService.addLog(scheduler.email, Actions.INSERT_SCHEDULE_ITEM, Status.FAILED)
                    return@post call.respond(HttpStatusCode.NotFound, "This Dinning Hall Doest Not Exist")
                }
            val request = call.receive<ScheduleItemRequest>()
            if(request.date <= LocalDate.now()){
                userService.addLog(scheduler.email, Actions.INSERT_SCHEDULE_ITEM, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, "You Can't Make An Apoyment For This Date")
            }

            if(request.breakfast){
                val item = userService.getScheduleItem(request.date, "breakfast", scheduler.dinninghallID)
                    ?: userService.insertScheduleItem(request.date, "breakfast", scheduler.dinninghallID)
            }
            if (request.lunch){
                val item = userService.getScheduleItem(request.date, "lunch", scheduler.dinninghallID)
                    ?:  userService.insertScheduleItem(request.date, "lunch", scheduler.dinninghallID)
            }
            if (request.dinner){
                val item = userService.getScheduleItem(request.date, "dinner", scheduler.dinninghallID)
                    ?: userService.insertScheduleItem(request.date, "dinner", scheduler.dinninghallID)
            }
            userService.addLog(scheduler.email, Actions.INSERT_SCHEDULE_ITEM, Status.SUCCESSFUL)
            return@post call.respond(HttpStatusCode.OK, "All Done")
        }
    }
}

fun Route.deleteScheduleItem(userService: UserService){
    authenticate {
        delete("siral/schedule") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@delete call.respond(HttpStatusCode.InternalServerError)

            val role = principal.getClaim(name = "userRole", String::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError)

            if(role != "SCHEDULER")
                return@delete call.respond(HttpStatusCode.Unauthorized, "Access Denied")

            val schedulerId = principal.getClaim(name = "userId", Long::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, "Scheduler ID is null")

            val scheduler = userService.getSiteManagerSchedulerByID(schedulerId)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, "Scheduler not found")

            val dinninghall = userService.getDinninghallByID(scheduler.dinninghallID)
                ?: run {
                    userService.addLog(scheduler.email, Actions.DELETE_SCHEDULE_ITEM, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.NotFound, "This Dinning Hall Doest Not Exist")
                }
            val request = call.receive<ScheduleItemRequest>()
            if (request.breakfast)
                userService.deleteScheduleItem(request.date, "breakfast", scheduler.dinninghallID)
            if (request.lunch)
                userService.deleteScheduleItem(request.date, "lunch", scheduler.dinninghallID)
            if (request.dinner)
                userService.deleteScheduleItem(request.date, "dinner", scheduler.dinninghallID)

            userService.addLog(scheduler.email, Actions.DELETE_SCHEDULE_ITEM, Status.SUCCESSFUL)
            return@delete call.respond(HttpStatusCode.OK, "All Done")
        }
    }
}