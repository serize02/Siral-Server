package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.request.CreateReservation
import com.siral.responses.Response
import com.siral.utils.Access
import com.siral.utils.Messages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.reservations(dataService: DataService){
    route("/reservations"){

        authenticate {
            get {
                call.withRole(Access.administration){
                    val reservations = dataService.reservationService.getAll()
                    return@get call.respond(HttpStatusCode.OK, Response(data = reservations, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
                }
            }
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLong()
            val reservation = id?.let { dataService.reservationService.getById(it) }
                ?: return@get call.respond(HttpStatusCode.NotFound, Response(success = false, data = null, message = Messages.RESERVATION_NOT_FOUND))
            return@get call.respond(HttpStatusCode.OK, Response(data = reservation, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
        }

        post {
            val data = call.receive<CreateReservation>()
            dataService.reservationService.create(data.studentId, data.scheduleItemId)
            val new = dataService.reservationService.getByScheduleItemIdAndUserId(data.studentId, data.scheduleItemId)
            return@post call.respond(HttpStatusCode.Created, Response(data = new, message = Messages.RESERVATION_CREATED_SUCCESSFULLY))
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toLong()
            id?.let { dataService.reservationService.getById(it) }
                ?: return@delete call.respond(HttpStatusCode.NotFound, Response(success = false, data = null, message = Messages.RESERVATION_NOT_FOUND))
            dataService.reservationService.delete(id)
            return@delete call.respond(HttpStatusCode.OK, Response(data = null, message = Messages.RESERVATION_DELETED_SUCCESSFULLY))
        }
    }
}