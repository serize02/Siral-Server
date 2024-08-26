package com.siral.routes

import com.siral.data.UserService
import com.siral.data.student.Student
import com.siral.request.AuthCredentials
import com.siral.responses.StudentLoginData
import com.siral.security.account.verifyAdminCredentials
import com.siral.security.account.verifySiteManagerSchedulerCredentials
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


internal fun Student.toStudentLoginData(token: String) = StudentLoginData(
    id = this.id,
    token = token,
    name = this.name,
    code = this.code,
    email = this.email,
    resident = this.resident,
    last = this.last,
    active = this.active
)

fun Route.studentLogin(
    userService: UserService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("siral/student-login") {
        val credentials = call.receive<AuthCredentials>()

        // Fake external API validation
        val authResponse = verifyStudentCredentials(credentials)
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid Credentials")

        // Check if the student is already registered
        val foundStudent = userService.getStudentByEmail(credentials.email)

        val student = foundStudent ?: userService.insertStudent(authResponse).let {
            userService.getStudentById(it)
        }

        val token = tokenService.generateToken(
            config = tokenConfig,
            claims = arrayOf(
                TokenClaim(
                    name = "userId",
                    value = student?.id.toString()
                ),
                TokenClaim(
                    name = "userRole",
                    value = "STUDENT"
                )
            )
        )

        if (student != null) {
            return@post call.respond(HttpStatusCode.OK, student.toStudentLoginData(token))
        } else {
            return@post call.respond(HttpStatusCode.Unauthorized, "Invalid Student")
        }
    }
}

fun Route.adminLogin(
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
   post("siral/admin-login") {
       val credentials = call.receive<AuthCredentials>()
       if (!verifyAdminCredentials(credentials))
           return@post call.respond(HttpStatusCode.Unauthorized, "Invalid Credentials")

       val token = tokenService.generateToken(
           config = tokenConfig,
           claims = arrayOf(
               TokenClaim(
                   name = "userRole",
                   value = "ADMIN"
               )
           )
       )

       return@post call.respond(HttpStatusCode.OK, token)
   }
}

fun Route.siteManagerSchedulerLogin(
    userService: UserService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("siral/site-manager-scheduler-login") {
        val credentials = call.receive<AuthCredentials>()

        if(!verifySiteManagerSchedulerCredentials(credentials))
            return@post call.respond(HttpStatusCode.Unauthorized, "Invalid Credentials")

        val user = userService.getSiteManagerSchedulerByEmail(credentials.email)
            ?: return@post call.respond(HttpStatusCode.Unauthorized, "Invalid User")

        val token = tokenService.generateToken(
            config = tokenConfig,
            claims = arrayOf(
                TokenClaim(
                    name = "userId",
                    value = user.id.toString()
                ),
                TokenClaim(
                    name = "userRole",
                    value = user.role
                )
            )
        )

        return@post call.respond(HttpStatusCode.OK, token)

    }
}

fun Route.auth(){
    authenticate {
        get("/auth") {
            return@get call.respond(HttpStatusCode.OK)
        }
    }
}
