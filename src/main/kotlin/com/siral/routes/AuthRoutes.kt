package com.siral.routes

import com.siral.data.DataService
import com.siral.request.AuthCredentials
import com.siral.responses.AuthResponse
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

fun Route.studentLogin(
    dataService: DataService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("siral/student-login") {
        val credentials = call.receive<AuthCredentials>()

        // Fake external API validation
        val authResponse = verifyStudentCredentials(credentials)
            ?: return@post call.respond(HttpStatusCode.Unauthorized, AuthResponse(
                success = false,
                data = null,
                message = ResponseMessage.INVALID_CREDENTIALS.name,
                status = HttpStatusCode.Unauthorized.value,
                role = null,
                token = null
            ))

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
            return@post call.respond(HttpStatusCode.OK, AuthResponse(
                success = true,
                data = student,
                message = ResponseMessage.USER_LOGED_SUCCESSFULLY.name,
                status = 200,
                role = UserRole.STUDENT.name,
                token = token
            ))
        }

        return@post call.respond(HttpStatusCode.Unauthorized, AuthResponse(
            success = false,
            data = null,
            message = ResponseMessage.INVALID_CREDENTIALS.name,
            status = HttpStatusCode.Unauthorized.value,
            role = null,
            token = null
        ))
    }
}

fun Route.adminLogin(
    dataService: DataService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
){
    post("siral/admin-login") {
        val credentials = call.receive<AuthCredentials>()

        if (!verifyAdminCredentials(credentials)){

            if(verifySiteManagerSchedulerCredentials(credentials)){
                val user = dataService.siteManagerSchedulerService.getSiteManagerSchedulerByEmail(credentials.email)
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, AuthResponse(
                        success = false,
                        data = null,
                        message = ResponseMessage.INVALID_CREDENTIALS.name,
                        status = HttpStatusCode.Unauthorized.value,
                        role = null,
                        token = null
                    ))

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
                return@post call.respond(HttpStatusCode.OK, AuthResponse(
                    success = true,
                    data = user,
                    message = ResponseMessage.USER_LOGED_SUCCESSFULLY.name,
                    status = 200,
                    role = user.role,
                    token = token
                ))
            }

            return@post call.respond(HttpStatusCode.Unauthorized, AuthResponse(
                success = false,
                data = null,
                message = ResponseMessage.INVALID_CREDENTIALS.name,
                status = HttpStatusCode.Unauthorized.value,
                role = null,
                token = null
            ))
        }

        val token = tokenService.generateToken(
            config = tokenConfig,
            claims = arrayOf(
                TokenClaim(
                    name = "userRole",
                    value = UserRole.ADMIN.name
                )
            )
        )
        dataService.logsService.addLog(credentials.email, Actions.LOGIN, Status.SUCCESSFUL)

        return@post call.respond(HttpStatusCode.OK, AuthResponse(
            success = true,
            data = null,
            message = ResponseMessage.USER_LOGED_SUCCESSFULLY.name,
            status = 200,
            role = UserRole.ADMIN.name,
            token = token
        ))

    }
}

fun Route.auth(){
    authenticate {
        get("/siral/auth") {
            return@get call.respond(HttpStatusCode.OK)
        }
    }
}
