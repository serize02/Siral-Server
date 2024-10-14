package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.responses.Response
import com.siral.utils.Access
import com.siral.utils.Messages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.logs(dataService: DataService){
    route("logs"){
        authenticate {
            get {
                call.withRole(Access.administration){
                    val logs = dataService.logsService.getAll()
                    return@get call.respond(HttpStatusCode.OK, Response(data = logs, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
                }
            }
        }
    }
}