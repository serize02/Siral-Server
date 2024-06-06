package com.siral.routes

import com.siral.data.UserService
import com.siral.data.admin.Admin
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.annotations.ApiStatus.Experimental

@Experimental
fun Route.addAdmins(userService: UserService){
    post("/siral/admins/{admin}"){
        val adminUsername = call.parameters["admin"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing username")
        val admin = Admin(username = adminUsername)
        userService.insertAdmin(admin)
        call.respond(HttpStatusCode.Created, "Admin Added Successfully")
    }
}