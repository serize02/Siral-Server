package com.siral.routes

import com.siral.data.DataService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getAllLogs(dataService: DataService){
    get("siral/logs"){
        return@get call.respond(HttpStatusCode.OK, dataService.logsService.getLogs())
    }
}