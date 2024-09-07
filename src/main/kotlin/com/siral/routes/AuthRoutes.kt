package com.siral.routes

import com.siral.data.DataService
import com.siral.data.models.Student
import com.siral.request.AuthCredentials
import com.siral.responses.StudentLoginData
import com.siral.security.account.verifyAdminCredentials
import com.siral.security.account.verifySiteManagerSchedulerCredentials
import com.siral.security.account.verifyStudentCredentials
import com.siral.security.token.TokenClaim
import com.siral.security.token.TokenConfig
import com.siral.security.token.TokenService
import com.siral.utils.Actions
import com.siral.utils.ResponseMessage
import com.siral.utils.Status
import com.siral.utils.UserRole
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
    dataService: DataService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("siral/student-login") {
        try {
            val credentials = call.receive<AuthCredentials>()

            // Fake external API validation
            val authResponse = verifyStudentCredentials(credentials)
                ?: return@post call.respond(HttpStatusCode.Unauthorized, ResponseMessage.INVALID_CREDENTIALS)

            // Check if the student is already registered
            val foundStudent = dataService.studentService.getStudentByEmail(credentials.email)

            val student = foundStudent ?: dataService.studentService.insertStudent(authResponse).let {
                dataService.studentService.getStudentById(it)
            }

            student?.let { it1 -> dataService.studentService.updateStudentLastAndActive(it1.id) }

            val token = tokenService.generateToken(
                config = tokenConfig,
                claims = arrayOf(
                    TokenClaim(
                        name = "userId",
                        value = student?.id.toString()
                    ),
                    TokenClaim(
                        name = "userRole",
                        value = UserRole.STUDENT.toString()
                    )
                )
            )

            if (student != null) {
                dataService.logsService.addLog(student.email, Actions.LOGIN, Status.SUCCESSFUL)
                return@post call.respond(HttpStatusCode.OK, student.toStudentLoginData(token))
            }

            return@post call.respond(HttpStatusCode.Unauthorized, ResponseMessage.INVALID_STUDENT)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
        }
    }
}

fun Route.adminLogin(
    dataService: DataService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("siral/admin-login") {
        try {
            val credentials = call.receive<AuthCredentials>()
            if (!verifyAdminCredentials(credentials))
                return@post call.respond(HttpStatusCode.Unauthorized, ResponseMessage.INVALID_CREDENTIALS)

            val token = tokenService.generateToken(
                config = tokenConfig,
                claims = arrayOf(
                    TokenClaim(
                        name = "userRole",
                        value = UserRole.ADMIN.toString()
                    )
                )
            )
            dataService.logsService.addLog(credentials.email, Actions.LOGIN, Status.SUCCESSFUL)
            return@post call.respond(HttpStatusCode.OK, token)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
        }
    }
}

fun Route.siteManagerSchedulerLogin(
    dataService: DataService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("siral/site-manager-scheduler-login") {
        try {
            val credentials = call.receive<AuthCredentials>()

            if(!verifySiteManagerSchedulerCredentials(credentials))
                return@post call.respond(HttpStatusCode.Unauthorized, ResponseMessage.INVALID_CREDENTIALS)

            val user = dataService.siteManagerSchedulerService.getSiteManagerSchedulerByEmail(credentials.email)
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

            dataService.logsService.addLog(credentials.email, Actions.LOGIN, Status.SUCCESSFUL)
            return@post call.respond(HttpStatusCode.OK, token)

        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, ResponseMessage.SOMETHING_WENT_WRONG)
        }
    }
}

fun Route.auth(){
    authenticate {
        get("/siral/auth") {
            return@get call.respond(HttpStatusCode.OK)
        }
    }
}
