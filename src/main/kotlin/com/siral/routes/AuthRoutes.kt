package com.siral.routes

import com.siral.data.UserService
import com.siral.data.user.User
import com.siral.request.AuthCredentials
import com.siral.responses.UserAuthResponse
import com.siral.security.account.verifyCredentials
import com.siral.security.token.TokenClaim
import com.siral.security.token.TokenConfig
import com.siral.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


internal fun User.toUserAuthResponse(token: String) = UserAuthResponse(
    token = token,
    role = this.role,
    id = this.id,
)

/**
 * Remember to implement the verifyCredentials fun
 * (make a request with the user's credentials to the university server).
 * Right now it will always return true.
 *
 * If authResponse is equal to false then it's necessary to respond with a Conflict
 * Status on the server and the message "Invalid Credentials".
 *
 * Then it is necessary to know if the user is already registered in the database,
 * if it is that the case (foundUser != null) then a token will be sent. If it is not
 * registered in the database it will be inserted and a token will be sent in the same way.
 */
fun Route.login(
    userService: UserService,
    tokenService: TokenService,
    tokenConfig: TokenConfig
) {
    post("/siral/login") {
        val credentials = call.receive<AuthCredentials>()
        val authResponse = verifyCredentials(credentials)
        if(!authResponse)
            return@post call.respond(HttpStatusCode.Conflict, "Invalid Credentials")
        val foundUser = userService.getUserByUsername(credentials.username)
        if(foundUser != null){
            val token = tokenService.generateToken(
                config = tokenConfig,
                TokenClaim(
                    name = "userId",
                    value = foundUser.id,
                )
            )
            return@post call.respond(HttpStatusCode.OK, foundUser.toUserAuthResponse(token))
        }
        val admin = userService.verifyAdmin(credentials.username)
        val user = User(
            username = credentials.username,
            role = if(admin) "ADMIN" else "USER",
            dinningHall = credentials.dinningHall
        )
        userService.insertUser(user)
        val token = tokenService.generateToken(
            config = tokenConfig,
            claims = arrayOf(
                TokenClaim(
                    name = "userId",
                    value = user.id,
                ),
                TokenClaim(
                    name = "userRole",
                    value = user.role,
                )
            )
        )
        return@post call.respond(HttpStatusCode.OK, user.toUserAuthResponse(token))
    }
}

fun Route.auth(){
    authenticate {
        get("/auth") {
            return@get call.respond(HttpStatusCode.OK)
        }
    }
}
