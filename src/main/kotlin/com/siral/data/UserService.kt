package com.siral.data

import com.siral.data.admin.Admin
import com.siral.data.admin.AdminDataSource
import com.siral.data.dinninghall.DinningHall
import com.siral.data.dinninghall.DinningHallDataSource
import com.siral.data.schedule.ScheduleItem
import com.siral.data.schedule.ScheduleDataSource
import com.siral.data.reservation.Reservation
import com.siral.data.reservation.ReservationDataSource
import com.siral.data.student.Student
import com.siral.data.student.StudentDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.annotations.ApiStatus.Experimental
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class UserService(
    private val database: Database
):
    AdminDataSource,
    DinningHallDataSource,
    StudentDataSource,
    ScheduleDataSource,
    ReservationDataSource
{
    object Admins: Table() {
        val id = varchar("id", 128)
        val username = varchar("username", 50)
        override val primaryKey = PrimaryKey(username)
    }

    object DinningHalls: Table(){
        val id = varchar("id", 128)
        val name = varchar("name", 50)
        override val primaryKey = PrimaryKey(id)
    }

    object Students: Table() {
        val id = varchar("id", 128)
        val username = varchar("username", 50)
        val dinningHall = varchar("dinning_hall", 100)
        val last = varchar("last", 16)
        val active = bool("status")
        override val primaryKey = PrimaryKey(id)
    }

    object Schedule: Table(){
        val id = varchar("id", 128)
        val date = varchar("date", 16)
        val time = varchar("time", 16)
        val dinningHall = varchar("dinning_hall", 32)
        val active = bool("active")

        override val primaryKey = PrimaryKey(id)
    }

    object Reservations: Table() {
        val id = varchar("id", 128)
        val userId = varchar("user_id", 128) references Students.id
        val scheduleItemId = varchar("schedule_item_id", 128) references Schedule.id
        val date_of_reservation = varchar("date_of_reservation", 16)
        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Admins, DinningHalls, Students, Schedule, Reservations)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    @Experimental
    override suspend fun insertAdmin(admin: Admin): Unit = dbQuery {
        Admins
            .insert {
                it[id] = admin.id
                it[username] = admin.username
            }
    }

    override suspend fun getAdminByName(userName: String): Admin? = dbQuery {
        Admins
            .select { Admins.username eq userName }
            .map {
                Admin(
                    id = it[Admins.id],
                    username = it[Admins.username],
                )
            }
            .singleOrNull()
    }

    override suspend fun getAdminById(adminId: String): Admin? = dbQuery {
        Admins
            .select { Admins.id eq adminId }
            .map {
                Admin(
                    id = it[Admins.id],
                    username = it[Admins.username],
                )
            }
            .singleOrNull()
    }


    override suspend fun insertDinningHall(dinningHall: DinningHall): Unit = dbQuery {
        DinningHalls
            .insert {
                it[id] = dinningHall.id
                it[name] = dinningHall.name
            }
    }

    override suspend fun deleteDinningHallByName(dinningHallName: String): Unit = dbQuery {
        DinningHalls
            .deleteWhere { name eq dinningHallName }
    }

    override suspend fun getDinningHallByName(dinningHallName: String): DinningHall? = dbQuery {
        DinningHalls
            .select { DinningHalls.name eq dinningHallName }
            .map {
                DinningHall(
                    id = it[DinningHalls.id],
                    name = it[DinningHalls.name]
                )
            }
            .singleOrNull()
    }


    override suspend fun insertUser(student: Student): Unit = dbQuery {
        Students
            .insert{
                it[id] = student.id
                it[username] = student.username
                it[dinningHall] = student.dinningHall
                it[last] = student.last
                it[active] = student.active
            }
    }

    override suspend fun updateLast(userId: String): Unit = dbQuery {
        Students
            .update ({ Students.id eq userId }) {
                it[last] = LocalDate.now().toString()
                it[active] = true
            }
    }

    override suspend fun updateActive(): Unit = dbQuery {
        val date = LocalDate.now().minusMonths(2).toString()
        Students
            .update ({ Students.last eq date}){
                it[active] = false
            }
    }

    override suspend fun deleteUser(): Unit = dbQuery {
        val date = LocalDate.now().minusMonths(6).toString()
        val userId = Students
            .select { Students.last eq date }
            .map { Students.id.toString() }
        userId.forEach { userId ->
            Reservations
                .deleteWhere { Reservations.userId eq userId}
            Students
                .deleteWhere { Students.id eq userId }
        }
    }

    override suspend fun getUserByUsername(username: String): Student? = dbQuery {
        Students
            .select { Students.username eq username }
            .map {
                Student(
                    id = it[Students.id],
                    username = it[Students.username],
                    dinningHall = it[Students.dinningHall],
                    last = it[Students.last],
                    active = it[Students.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun getUserById(userId: String): Student? = dbQuery {
        Students
            .select { Students.id eq userId }
            .map {
                Student(
                    id = it[Students.id],
                    username = it[Students.username],
                    dinningHall = it[Students.dinningHall],
                    last = it[Students.last],
                    active = it[Students.active]
                )
            }
            .singleOrNull()
    }



    override suspend fun getScheduleForTheNextDays(dinningHallName: String): List<ScheduleItem> = dbQuery{
        val today = LocalDate.now().toString()
        return@dbQuery Schedule
            .select { ((Schedule.date greaterEq today) and (Schedule.dinningHall eq dinningHallName))}
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.date],
                    time = it[Schedule.time],
                    dinningHall = it[Schedule.dinningHall],
                    active = it[Schedule.active]
                )
            }
    }

    override suspend fun getScheduleItemById(mealId: String): ScheduleItem? = dbQuery {
        Schedule
            .select { Schedule.id eq mealId }
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.date],
                    time = it[Schedule.time],
                    dinningHall = it[Schedule.dinningHall],
                    active = it[Schedule.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun getScheduleItem(scheduleItem: ScheduleItem): ScheduleItem? = dbQuery {
        Schedule
            .select { ((Schedule.date eq scheduleItem.date) and (Schedule.time eq scheduleItem.time) and (Schedule.dinningHall eq scheduleItem.dinningHall)) }
            .map {
                ScheduleItem(
                    id = it[Schedule.id],
                    date = it[Schedule.date],
                    time = it[Schedule.time],
                    dinningHall = it[Schedule.dinningHall],
                    active = it[Schedule.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun insertScheduleItem(scheduleItem: ScheduleItem): Unit = dbQuery {
        Schedule
            .insert {
                it[id] = scheduleItem.id
                it[date] = scheduleItem.date
                it[time] = scheduleItem.time
                it[dinningHall] = scheduleItem.dinningHall
                it[active] = scheduleItem.active
            }
    }

    override suspend fun activateScheduleItem(days: Long): Unit = dbQuery {
        val date = LocalDate.now().plusDays(days).toString()
        Schedule
            .update({ Schedule.date eq date }){
                it[active] = true
            }
    }



    override suspend fun insertReservation(reservation: Reservation): Unit = dbQuery {
        Reservations
            .insert {
                it[id] = reservation.id
                it[userId] = reservation.userId
                it[scheduleItemId] = reservation.scheduleItemId
                it[date_of_reservation] = reservation.dateOfReservation
            }
        updateLast(reservation.userId)
    }

    override suspend fun deleteReservation(reservationId: String): Unit = dbQuery {
        Reservations
            .deleteWhere { id eq reservationId }
    }

    override suspend fun getReservations(userId: String): List<Reservation> = dbQuery{
        Reservations
            .select { Reservations.userId eq userId }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    userId = it[Reservations.userId],
                    scheduleItemId = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.date_of_reservation],
                )
            }
    }

    override suspend fun getReservationByMealIdAndUserId(mealId: String, userId: String): Reservation? = dbQuery {
        Reservations
            .select { ((Reservations.scheduleItemId eq mealId) and (Reservations.userId eq userId)) }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    userId = it[Reservations.userId],
                    scheduleItemId = it[Reservations.scheduleItemId],
                    dateOfReservation = it[Reservations.date_of_reservation],
                )
            }
            .singleOrNull()
    }
}