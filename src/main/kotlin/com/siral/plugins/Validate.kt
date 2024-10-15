package com.siral.plugins

import com.siral.data.database.tables.*
import com.siral.request.*
import com.siral.utils.UserRole
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

fun Application.configureValidation(){
    install(RequestValidation){

        validate<AuthCredentials> { request ->
            if(!request.email.endsWith("@uclv.cu")) ValidationResult.Invalid("Try to use university email")
            else ValidationResult.Valid
        }

        validate<CreateDiningHall> { request ->
            val count = transaction { Dininghalls.select { Dininghalls.name eq request.name }.count() }
            if (count > 0) ValidationResult.Invalid("This name is already taken")
            else ValidationResult.Valid
        }

        validate<CreateScheduleItem> { request ->
            if (request.date <= LocalDate.now()) ValidationResult.Invalid("Invalid day")
            else ValidationResult.Valid
        }

        validate<CreateReservation> { request ->
            val student = transaction { Students.select { Students.id  eq request.studentId}.count() > 0 }
            val item = transaction { Schedule.select { Schedule.id eq request.scheduleItemId}.count() > 0 }
            val reservation = transaction { Reservations.select { (Reservations.scheduleItemId eq request.scheduleItemId) and (Reservations.studentID eq request.studentId) }.count() > 0 }
            if (!student || !item || reservation) ValidationResult.Invalid("Invalid data")
            else ValidationResult.Valid
        }

        validate<CreateSiteManagerScheduler> { request ->
            val email = transaction { SiteManagerSchedulers.select { SiteManagerSchedulers.email eq request.email }.count() > 0 }
            val dininghall = transaction { Dininghalls.select { Dininghalls.id  eq request.dininghallId}.count() > 0 }
            if (!request.email.endsWith("@uclv.cu") || email || !dininghall || !(listOf(UserRole.SCHEDULER.name, UserRole.SITE_MANAGER.name).contains(request.role)))
                ValidationResult.Invalid("Invalid Credentials")
            else ValidationResult.Valid
        }

        validate<UpdateDaysBefore> { request ->
            if (!listOf(2,7,15,30).contains(request.days)) ValidationResult.Invalid("Invalid days")
            else ValidationResult.Valid
        }

        validate<AvailableMeals> { request ->
            val dininghall = transaction { Dininghalls.select { Dininghalls.id  eq request.dininghallId}.count() > 0 }
            if (!dininghall || request.date <= LocalDate.now())
                ValidationResult.Invalid("Invalid data")
            else ValidationResult.Valid
        }
    }
}