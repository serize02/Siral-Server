package com.siral.routes

import com.siral.data.DataService
import com.siral.plugins.withRole
import com.siral.request.ScheduleItemRequest
import com.siral.utils.Actions
import com.siral.utils.ResponseMessage
import com.siral.utils.Status
import com.siral.utils.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate


fun Route.getSchedule(dataService: DataService) {
    get("siral/schedule/{dinninghallID}") {
        val dinninghallID = call.parameters["dinninghallID"]?.toLong()
            ?: return@get call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_REQUIRED_FIELDS)
        dataService.dinningHallService.getDinninghallByID(dinninghallID)
            ?: return@get call.respond(HttpStatusCode.NotFound, ResponseMessage.DINNING_HALL_NOT_FOUND)
        val items = dataService.scheduleService.getSchedule(dinninghallID)
        return@get call.respond(HttpStatusCode.OK, items)
    }
}


fun Route.insertScheduleItem(dataService: DataService){
    post("siral/schedule/{schedulerID}") {
        call.withRole(UserRole.SCHEDULER){
            val schedulerId = call.parameters["schedulerID"]?.toLong()
                ?: return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_REQUIRED_FIELDS)

            val scheduler = dataService.siteManagerSchedulerService.getSiteManagerSchedulerByID(schedulerId)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, ResponseMessage.USER_NOT_FOUND)

            dataService.dinningHallService.getDinninghallByID(scheduler.dinninghallID)
                ?: run {
                    dataService.logsService.addLog(scheduler.email, Actions.INSERT_SCHEDULE_ITEM, Status.FAILED)
                    return@post call.respond(HttpStatusCode.NotFound, ResponseMessage.DINNING_HALL_NOT_FOUND)
                }

            val request = call.receive<ScheduleItemRequest>()

            if(request.date <= LocalDate.now()){
                dataService.logsService.addLog(scheduler.email, Actions.INSERT_SCHEDULE_ITEM, Status.FAILED)
                return@post call.respond(HttpStatusCode.BadRequest, ResponseMessage.INVALID_DATE)
            }

            if(request.breakfast){
                dataService.scheduleService.getScheduleItem(request.date, "breakfast", scheduler.dinninghallID)
                    ?: dataService.scheduleService.insertScheduleItem(request.date, "breakfast", scheduler.dinninghallID)
            }
            if (request.lunch){
                dataService.scheduleService.getScheduleItem(request.date, "lunch", scheduler.dinninghallID)
                    ?:  dataService.scheduleService.insertScheduleItem(request.date, "lunch", scheduler.dinninghallID)
            }
            if (request.dinner){
                dataService.scheduleService.getScheduleItem(request.date, "dinner", scheduler.dinninghallID)
                    ?: dataService.scheduleService.insertScheduleItem(request.date, "dinner", scheduler.dinninghallID)
            }

            dataService.logsService.addLog(scheduler.email, Actions.INSERT_SCHEDULE_ITEM, Status.SUCCESSFUL)
            return@post call.respond(HttpStatusCode.OK, ResponseMessage.ALL_DONE)

        }
    }
}

fun Route.deleteScheduleItem(dataService: DataService){
    delete("siral/schedule/{schedulerID}") {
        call.withRole(UserRole.SCHEDULER){
            val schedulerId = call.parameters["schedulerID"]?.toLong()
                ?: return@delete call.respond(HttpStatusCode.BadRequest, ResponseMessage.MISSING_REQUIRED_FIELDS)

            val scheduler = dataService.siteManagerSchedulerService.getSiteManagerSchedulerByID(schedulerId)
                ?: return@delete call.respond(HttpStatusCode.InternalServerError, ResponseMessage.USER_NOT_FOUND)

            dataService.dinningHallService.getDinninghallByID(scheduler.dinninghallID)
                ?: run {
                    dataService.logsService.addLog(scheduler.email, Actions.DELETE_SCHEDULE_ITEM, Status.FAILED)
                    return@delete call.respond(HttpStatusCode.NotFound, ResponseMessage.DINNING_HALL_NOT_FOUND)
                }

            val request = call.receive<ScheduleItemRequest>()

            if (request.breakfast)
                dataService.scheduleService.deleteScheduleItem(request.date, "breakfast", scheduler.dinninghallID)

            if (request.lunch)
                dataService.scheduleService.deleteScheduleItem(request.date, "lunch", scheduler.dinninghallID)

            if (request.dinner)
                dataService.scheduleService.deleteScheduleItem(request.date, "dinner", scheduler.dinninghallID)

            dataService.logsService.addLog(scheduler.email, Actions.DELETE_SCHEDULE_ITEM, Status.SUCCESSFUL)
            return@delete call.respond(HttpStatusCode.OK, ResponseMessage.ALL_DONE)

        }
    }
}