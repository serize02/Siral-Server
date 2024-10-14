package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.request.CreateDiningHall
import com.siral.request.UpdateDaysBefore
import com.siral.responses.Response
import com.siral.utils.Access
import com.siral.utils.Messages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*

fun Routing.dininghalls(dataService: DataService){
    route("/dining-halls"){

        get {
            val dininghalls = dataService.dinningHallService.getAll()
            return@get call.respond(HttpStatusCode.OK, Response(data = dininghalls, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLong()
            val dininghall = id?.let { dataService.dinningHallService.getById(it) }
                ?: return@get call.respond(HttpStatusCode.NotFound, Response(success = false, data = null, message = Messages.DININGHALL_NOT_FOUND))
            return@get call.respond(HttpStatusCode.OK, Response(data = dininghall, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
        }

        authenticate {
            post {
                call.withRole(Access.admin){
                    val info = call.receive<CreateDiningHall>()
                    dataService.dinningHallService.create(info.name)
                    val new = dataService.dinningHallService.getByName(info.name)
                    return@post call.respond(HttpStatusCode.Created, Response(data = new, message = Messages.DININGHALL_CREATED_SUCCESSFULLY))
                }
            }
        }

        authenticate {
            put("/{id}") {
                call.withRole(Access.siteManagers){
                    val id = call.parameters["id"]?.toLong()
                    id?.let { dataService.dinningHallService.getById(it) }
                        ?: return@put call.respond(HttpStatusCode.NotFound, Response(success = false, data = null, message = Messages.DININGHALL_NOT_FOUND))
                    val info = call.receive<CreateDiningHall>()
                    dataService.dinningHallService.update(id,info.name)
                    val new = dataService.dinningHallService.getById(id)
                    return@put call.respond(HttpStatusCode.OK, Response(data = new, message = Messages.DININGHALL_UPDATED_SUCCESSFULLY))
                }
            }
        }

        authenticate {
            put {
                call.withRole(Access.siteManagers){
                    val days = call.receive<UpdateDaysBefore>()
                    val dininghallId = call.principal<JWTPrincipal>()?.getClaim("userId", Long::class)?.let {
                        dataService.siteManagerSchedulerService.getByID(it)?.dinninghallID
                    } ?: return@put call.respond(HttpStatusCode.NotFound, Response(success = false, data = null, message = Messages.DININGHALL_NOT_FOUND))
                    dataService.siteManagerSchedulerService.updateDaysBeforeReservation(dininghallId, days.days)
                }
            }
        }

        authenticate {
            delete("/{id}") {
                call.withRole(Access.admin){
                    val id = call.parameters["id"]?.toLong()
                    val dininghall = id?.let { dataService.dinningHallService.getById(it) }
                        ?: return@delete call.respond(HttpStatusCode.NotFound, Response(success = false, data = null, message = Messages.DININGHALL_NOT_FOUND))
                    dataService.dinningHallService.delete(id)
                    return@delete call.respond(HttpStatusCode.OK, Response(data = null, message = Messages.DININGHALL_DELETED_SUCCESSFULLY))
                }
            }
        }
    }
}