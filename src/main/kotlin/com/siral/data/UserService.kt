package com.siral.data

import com.siral.data.dinninghall.DinningHall
import com.siral.data.dinninghall.DinninghallDataSource
import com.siral.data.reservation.Reservation
import com.siral.data.reservation.ReservationDataSource
import com.siral.data.schedule.ScheduleDataSource
import com.siral.data.schedule.ScheduleItem
import com.siral.data.student.Student
import com.siral.data.student.StudentDataSource
import com.siral.responses.StudentData
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class UserService(
    private val database: Database
):
    StudentDataSource,
    ReservationDataSource,
    ScheduleDataSource,
    DinninghallDataSource
{
    object Students: Table("students") {
        val id = long("id").autoIncrement()
        val name = varchar("name", 64)
        val code = long("student_code")
        val email = varchar("email", 32)
        val resident = bool("resident")
        val last = datetime("last_action").defaultExpression(CurrentDateTime)
        val active = bool("active").defaultExpression(booleanLiteral(true))

        override val primaryKey = PrimaryKey(id)
    }

    object Dinninghalls: Table(){
        val id = long("id").autoIncrement()
        val name = varchar("name", 50)

        override val primaryKey = PrimaryKey(id)
    }

    object Schedule: Table(){
        val id = long("id").autoIncrement()
        val itemDate = date("item_date")
        val time = varchar("time", 50)
        val dinninghallId = reference("dinning_hall_id", Dinninghalls.id)
        val available = bool("available")

        override val primaryKey = PrimaryKey(id)
    }

    object Reservations: Table() {
        val id = long("id").autoIncrement()
        val studentID = reference("student_id", Students.id)
        val scheduleItemId = reference("schedule_item_id", Schedule.id)
        val dateOfReservation = datetime("reservation_date").defaultExpression(CurrentDateTime)
        override val primaryKey = PrimaryKey(id)
    }


    object Admins: Table() {
        val id = long("id").autoIncrement()
        val username = varchar("username", 50)
        override val primaryKey = PrimaryKey(username)
    }


    init {
        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(Students)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }


    override suspend fun getStudentByEmail(email: String): Student? = dbQuery {
        Students
            .select { Students.email eq email}
            .map{
                Student(
                    id = it[Students.id],
                    name = it[Students.name],
                    code = it[Students.code],
                    email = it[Students.email],
                    resident = it[Students.resident],
                    last = it[Students.last],
                    active = it[Students.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun insertStudent(student: StudentData): Long = dbQuery {
        Students
            .insert {
                it[name] = student.name
                it[code] = student.code
                it[email] = student.email
                it[resident] = student.resident
            } get Students.id
    }

    override suspend fun getStudentById(studentId: Long): Student? = dbQuery {
        Students
            .select { Students.id eq studentId }
            .map{
                Student(
                    id = it[Students.id],
                    name = it[Students.name],
                    code = it[Students.code],
                    email = it[Students.email],
                    resident = it[Students.resident],
                    last = it[Students.last],
                    active = it[Students.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun makeReservation(studentID: Long, scheduleItemId: Long): Long = dbQuery {
        Reservations
            .insert {
                it[Reservations.studentID] = studentID
                it[Reservations.scheduleItemId] = scheduleItemId
            } get Reservations.id
    }

    override suspend fun getReservationByScheduleItemIdAndUserId(studentID: Long, scheduleItemID: Long): Reservation? = dbQuery {
        Reservations
            .select {
                (Reservations.studentID eq studentID) and
                        (Reservations.scheduleItemId eq scheduleItemID)
            }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    studentID = it[Reservations.studentID],
                    scheduleItemID = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.dateOfReservation]
                )
            }
            .singleOrNull()
    }

    override suspend fun deleteReservation(reservationId: Long): Unit = dbQuery {
        Reservations
            .deleteWhere { id eq reservationId }
    }

    override suspend fun getReservations(studentID: Long): List<Reservation> = dbQuery {
        Reservations
            .select { Reservations.studentID eq studentID }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    studentID = it[Reservations.studentID],
                    scheduleItemID = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.dateOfReservation]
                )
            }
            .toList()
    }

    override suspend fun getScheduleItemById(scheduleItemId: Long): ScheduleItem? = dbQuery {
        Schedule
            .select { Schedule.id eq scheduleItemId }
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.itemDate],
                    time = it[Schedule.time],
                    dinninghallId = it[Schedule.dinninghallId],
                    available = it[Schedule.available]
                )
            }
            .singleOrNull()
    }

    override suspend fun insertScheduleItem(date: LocalDate, time: String, dinninghallID: Long): Unit = dbQuery {
        Schedule
            .insert {
                it[Schedule.itemDate] = date
                it[Schedule.time] = time
                it[Schedule.dinninghallId] = dinninghallID
            }
    }

    override suspend fun deleteScheduleItem(date: LocalDate, time: String, dinninghallID: Long): Unit = dbQuery {
        Schedule
            .deleteWhere {
                (itemDate eq date) and
                (Schedule.time eq time) and
                (dinninghallId eq dinninghallID)
            }
    }

    override suspend fun getSchedule(dinninghallID: Long): List<ScheduleItem> = dbQuery {
        Schedule
            .select {
                (Schedule.dinninghallId eq dinninghallID) and
                (Schedule.itemDate greaterEq LocalDate.now())
            }
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.itemDate],
                    time = it[Schedule.time],
                    dinninghallId = it[Schedule.dinninghallId],
                    available = it[Schedule.available]
                )
            }

    }

    override suspend fun getDinninghallByID(dinninghallID: Long): DinningHall? = dbQuery {
        Dinninghalls
            .select { Dinninghalls.id eq dinninghallID }
            .map {
                DinningHall(
                    id = it[Dinninghalls.id],
                    name = it[Dinninghalls.name]
                )
            }
            .singleOrNull()
    }

    override suspend fun insertDinninghall(dinninghallName: String): Unit = dbQuery{
        Dinninghalls
            .insert {
                it[name] = dinninghallName
            }
    }

    override suspend fun deleteDinninghall(dinninghallID: Long): Unit = dbQuery {
        Dinninghalls
            .deleteWhere { id eq dinninghallID }
    }
}
