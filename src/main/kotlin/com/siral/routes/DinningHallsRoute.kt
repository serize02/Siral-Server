package com.siral.routes

import com.siral.data.UserService
import com.siral.data.dinninghall.DinningHall
import com.siral.request.DinningHallRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.insertDinningHalls(userService: UserService) {
    authenticate {
        post("siral/dinninghalls") {
            val principal = call.principal<JWTPrincipal>()
            val role = principal?.getClaim("userRole", String::class)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Access denied")
            if(role != "ADMIN")
                return@post call.respond(HttpStatusCode.Unauthorized, "You can't access this route")
            val adminId = principal.getClaim("userId", String::class)
                ?: return@post call.respond(HttpStatusCode.InternalServerError)
            val admin = userService.getAdminById(adminId)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val request = call.receive<DinningHallRequest>()
            if (request.name.isBlank())
                return@post call.respond(HttpStatusCode.BadRequest, "DinningHall Name is Required")
            val foundDinningHall = userService.getDinningHallByName(request.name)
            if(foundDinningHall == null){
                userService.insertDinningHall(DinningHall(name = request.name))
                return@post call.respond(HttpStatusCode.Created, "DinningHall Added Successfully")
            }
            return@post call.respond(HttpStatusCode.OK, "DinningHall Already Exists")
        }
    }
}

fun Route.deleteDinningHall(userService: UserService) {
    authenticate {
        delete("siral/dinninghalls/{dinningHallName}") {
            val principal = call.principal<JWTPrincipal>()
            val role = principal?.getClaim("userRole", String::class)
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Access denied role")
            if(role != "ADMIN")
                return@delete call.respond(HttpStatusCode.Unauthorized, "You can't access this route")
            val adminId = principal.getClaim("userId", String::class)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError)
            val admin = userService.getAdminById(adminId)
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Access denied")
            val name = call.parameters["dinningHallName"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "DinningHall Name is Required")
            if(name.isBlank())
                return@delete call.respond(HttpStatusCode.BadRequest, "DinningHall Name is Required")
            userService.deleteStudentByDinningHall(name)
            userService.deleteScheduleItemByDinningHall(name)
            userService.deleteDinningHallByName(name)
            return@delete call.respond(HttpStatusCode.OK, "DinningHall Removed Successfully")
        }
    }
}

