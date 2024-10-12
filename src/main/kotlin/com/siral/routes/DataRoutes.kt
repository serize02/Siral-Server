package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.responses.Response
import com.siral.utils.ResponseMessage
import com.siral.utils.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getStatsData(dataService: DataService) {
    get("siral/data") {
        call.withRole(listOf(UserRole.ADMIN.name, UserRole.SCHEDULER.name, UserRole.SITE_MANAGER.name)){
            val data = dataService.getStatsData()
            return@get call.respond(HttpStatusCode.OK, Response(success = true, data = data, message = ResponseMessage.DATA_RETREIVED_SUCCESSFULLY.name, status = 200))
        }
    }
}