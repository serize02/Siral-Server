package com.siral.routes

import com.siral.data.DataService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getData(dataService: DataService) {
    get("siral/data") {
        return@get call.respond(HttpStatusCode.OK, dataService.getData())
    }
}