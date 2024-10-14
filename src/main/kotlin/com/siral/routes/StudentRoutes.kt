package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.request.AuthCredentials
import com.siral.responses.AuthResponse
import com.siral.responses.Response
import com.siral.security.token.TokenClaim
import com.siral.security.token.TokenConfig
import com.siral.security.token.TokenService
import com.siral.utils.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

fun Routing.students(dataService: DataService, tokenConfig: TokenConfig, tokenService: TokenService){
    route("/students"){

        // get all

        authenticate {
            get {
                call.withRole(Access.administration){
                    val students = dataService.studentService.getAll()
                    return@get call.respond(HttpStatusCode.OK, Response(data = students, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
                }
            }
        }

        // get by id
        get("/{id}") {
            val id = call.parameters["id"]?.toLong()
            val student = id?.let { it1 -> dataService.studentService.getById(it1) }
                ?: return@get call.respond(HttpStatusCode.NotFound, Response(success = false, data = null, message = Messages.STUDENT_NOT_FOUND))
            return@get call.respond(HttpStatusCode.OK, Response(data = student, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
        }

        get("/{id}/reservations"){
            val id = call.parameters["id"]?.toLong()
            id?.let { it1 -> dataService.studentService.getById(it1) }
                ?: return@get call.respond(HttpStatusCode.NotFound, Response(success = false, data = null, message = Messages.STUDENT_NOT_FOUND))
            val reservations = dataService.reservationService.getByStudent(id)
            call.respond(HttpStatusCode.OK, Response(data = reservations, message = Messages.DATA_RETRIEVED_SUCCESSFULLY))
        }

        post("/login"){
            val credentials = call.receive<AuthCredentials>()
            val student = dataService.studentService.getByEmail(credentials.email)
                ?: dataService.studentService.create(credentials.retrieveData()).let {
                    dataService.studentService.getById(it)
                }
            student?.let {
                it1 -> dataService.studentService.updateStudentLastAndActive(it1.id)
                val token = tokenService.generateToken(
                    config = tokenConfig,
                    claims = arrayOf(TokenClaim(name = "userId", value = student.id.toString()), TokenClaim(name = "userRole", value = UserRole.STUDENT.name))
                )
                dataService.logsService.create(student.email, Actions.LOGIN, Status.SUCCESSFUL)
                return@post call.respond(HttpStatusCode.OK, AuthResponse(
                    data = it1,
                    message = Messages.USER_LOGGED_SUCCESSFULLY,
                    role = UserRole.STUDENT,
                    token = token
                ) )
            }
        }

    }

}