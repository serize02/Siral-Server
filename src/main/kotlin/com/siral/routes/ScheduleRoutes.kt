package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.request.CreateScheduleItem
import com.siral.responses.Response
import com.siral.utils.Access
import com.siral.utils.Messages
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.call
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Routing.schedule(dataService: DataService){
    route("/schedule"){

        get {
            val schedule = dataService.scheduleService.getAll()
            return@get call.respond(HttpStatusCode.OK, Response(data = schedule, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
      }

        get("/{id}") {
            val id = call.parameters["id"]?.toLong()
            val item = id?.let { dataService.scheduleService.getById(it) }
                ?: return@get call.respond(HttpStatusCode.NotFound, Response(success = false, data = null, message = Messages.SCHEDULE_ITEM_NOT_FOUND) )
            return@get call.respond(HttpStatusCode.OK, Response(data = item, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
        }

        authenticate {
            post {
                call.withRole(Access.schedulers){
                    val data = call.receive<CreateScheduleItem>()
                    val dininghallId = call.principal<JWTPrincipal>()?.getClaim("userId", Long::class)?.let { it1 ->
                        dataService.siteManagerSchedulerService.getByID(it1)?.dinninghallID
                    } ?: return@post call.respond(HttpStatusCode.NotFound, Response(false, data = null, message = Messages.DININGHALL_NOT_FOUND))
                    if (data.breakfast)
                        dataService.scheduleService.create(data.date, "breakfast", dininghallId)
                    if (data.lunch)
                        dataService.scheduleService.create(data.date, "lunch", dininghallId)
                    if (data.dinner)
                        dataService.scheduleService.create(data.date, "dinner", dininghallId)
                    return@post call.respond(HttpStatusCode.OK, Response(data = null, message = Messages.ALL_DONE))
                }
            }
        }

        authenticate {
            delete("/{id}") {
                call.withRole(Access.schedulers){
                    val id = call.parameters["id"]?.toLong()
                    id?.let { dataService.scheduleService.getById(it) }
                        ?: return@delete call.respond(HttpStatusCode.NotFound, Response(success = false,data = null, message = Messages.SCHEDULE_ITEM_NOT_FOUND) )
                    dataService.scheduleService.delete(id)
                    return@delete call.respond(HttpStatusCode.OK, Response(data = null, message = Messages.SCHEDULE_ITEM_DELETED_SUCCESSFULLY))
                }
            }
        }

    }
}