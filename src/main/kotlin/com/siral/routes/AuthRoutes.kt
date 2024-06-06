package com.siral.routes

import com.siral.data.UserService
import com.siral.data.admin.Admin
import com.siral.data.student.Student
import com.siral.request.AuthCredentials
import com.siral.request.StudentAuthCredentials
import com.siral.responses.UserAuthResponse
import com.siral.security.account.verifyAdminCredentials
import com.siral.security.account.verifyStudentCredentials
import com.siral.security.token.TokenClaim
import com.siral.security.token.TokenConfig
import com.siral.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


internal fun Student.toUserAuthResponse(token: String) = UserAuthResponse(
    id = this.id,
    token = token,
)

fun Route.studentLogin(
    userService: UserService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("/siral/student-login") {
        val credentials = call.receive<StudentAuthCredentials>()
        val authResponse = verifyStudentCredentials(credentials, userService)
        if(!authResponse)
            return@post call.respond(HttpStatusCode.Conflict, "Invalid Credentials")
        val foundStudent = userService.getUserByUsername(credentials.username)
        val student = Student(
            username = credentials.username,
            dinningHall = credentials.dinningHall
        )
        val token = tokenService.generateToken(
            config = tokenConfig,
            claims = arrayOf(
                TokenClaim(
                    name = "userId",
                    value = foundStudent?.id ?: student.id,
                ),
                TokenClaim(
                    name = "userRole",
                    value = "STUDENT"
                )
            )
        )
        if(foundStudent != null)
            return@post call.respond(HttpStatusCode.OK, foundStudent.toUserAuthResponse(token))
        userService.insertUser(student)
        return@post call.respond(HttpStatusCode.OK, student.toUserAuthResponse(token))
    }
}

internal fun Admin.toUserAuthResponse(token: String) = UserAuthResponse(
    id = this.id,
    token = token,
)

fun Route.adminLogin(
    userService: UserService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("/siral/admin-login") {
        val credentials = call.receive<AuthCredentials>()
        val authResponse = verifyAdminCredentials(credentials, userService)
        if(!authResponse)
            return@post call.respond(HttpStatusCode.Conflict, "Invalid Credentials")
        val admin = userService.getAdminByName(credentials.username)
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid Admin")
        val token = tokenService.generateToken(
            config = tokenConfig,
            claims = arrayOf(
                TokenClaim(
                    name = "userId",
                    value = admin.id
                ),
                TokenClaim(
                    name = "userRole",
                    value = "ADMIN"
                )
            )
        )
        return@post call.respond(HttpStatusCode.OK, admin.toUserAuthResponse(token))
    }
}

fun Route.auth(){
    authenticate {
        get("/auth") {
            return@get call.respond(HttpStatusCode.OK)
        }
    }
}
