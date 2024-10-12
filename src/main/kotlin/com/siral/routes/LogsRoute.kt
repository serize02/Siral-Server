package com.siral.routes

import com.siral.data.DataService
import com.siral.responses.Response
import com.siral.utils.ResponseMessage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getAllLogs(dataService: DataService){
    get("siral/logs"){
        val logs = dataService.logsService.getLogs()
        return@get call.respond(HttpStatusCode.OK, Response(success = true, data = logs, message = ResponseMessage.DATA_RETREIVED_SUCCESSFULLY.name, status = 200))
    }
}


