package com.siral.routes

import com.siral.data.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.addAdmins(userService: UserService){
    post("/siral/admins/{admin}"){
        val adminUsername = call.parameters["admin"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing username")
        userService.insertAdmin(adminUsername)
        call.respond(HttpStatusCode.Created, "Added new admin")
    }
}