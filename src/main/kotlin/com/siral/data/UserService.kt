package com.siral.data

import com.siral.data.admin.Admin
import com.siral.data.admin.AdminDataSource
import com.siral.data.dinninghall.DinningHall
import com.siral.data.dinninghall.DinningHallDataSource
import com.siral.data.meal.Meal
import com.siral.data.meal.MealDataSource
import com.siral.data.reservation.Reservation
import com.siral.data.reservation.ReservationDataSource
import com.siral.data.user.User
import com.siral.data.user.UserDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

class UserService(
    private val database: Database
):  AdminDataSource,
    DinningHallDataSource,
    UserDataSource,
    MealDataSource,
    ReservationDataSource
{
    object Admins: Table() {
        val username = varchar("username", 50)
        override val primaryKey = PrimaryKey(username)
    }

    object DinningHalls: Table(){
        val id = varchar("id", 128)
        val name = varchar("name", 50)
        override val primaryKey = PrimaryKey(id)
    }

    object Users: Table() {
        val id = varchar("id", 128)
        val username = varchar("username", 50)
        val role = varchar("role", 20)
        val dinningHall = varchar("dinning_hall", 100) references DinningHalls.name
        val last = varchar("last", 16)
        val active = bool("status")
        override val primaryKey = PrimaryKey(id)
    }

    object Schedule: Table(){
        val id = varchar("id", 128)
        val date = varchar("date", 16)
        val time = varchar("time", 16)
        val dinningHall = varchar("dinning_hall", 32) references DinningHalls.name
        val active = bool("active")

        override val primaryKey = PrimaryKey(id)
    }

    object Reservations: Table() {
        val id = varchar("id", 128)
        val user_id = varchar("user_id", 128) references Users.id
        val meal_id = varchar("meal_id", 128) references Schedule.id
        val date_of_reservation = varchar("date_of_reservation", 16)
        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Admins, Users, Schedule, Reservations)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }


    override suspend fun insertAdmin(userName: String): Unit = dbQuery {
        Admins
            .insert {
                it[username] = userName
            }
    }

    override suspend fun verifyAdmin(userName: String): Boolean{
        val user = dbQuery {
            Admins
                .select { Admins.username eq userName }
                .map {
                    Admin(
                        username = it[Admins.username]
                    )
                }
                .singleOrNull()
        }
        return user != null
    }


    override suspend fun insertDinningHall(dinningHall: DinningHall): Unit = dbQuery {
        DinningHalls
            .insert {
                it[id] = dinningHall.id
                it[name] = dinningHall.name
            }
    }

    override suspend fun deleteDinningHall(dinningHallId: String): Unit = dbQuery {
        DinningHalls
            .deleteWhere { DinningHalls.id eq dinningHallId }
    }


    override suspend fun insertUser(user: User): Unit = dbQuery {
        Users
            .insert{
                it[id] = user.id
                it[username] = user.username
                it[role] = user.role
                it[dinningHall] = user.dinningHall
                it[last] = user.last
                it[active] = user.active
            }
    }

    override suspend fun updateLast(userId: String): Unit = dbQuery {
        Users
            .update ({ Users.id eq userId }) {
                it[last] = LocalDate.now().toString()
                it[active] = true
            }
    }

    override suspend fun updateActive(): Unit = dbQuery {
        val date = LocalDate.now().minusMonths(2).toString()
        Users
            .update ({ Users.last eq date}){
                it[active] = false
            }
    }

    override suspend fun deleteUser(): Unit = dbQuery {
        val date = LocalDate.now().minusMonths(6).toString()
        val userId = Users
            .select { Users.last eq date }
            .map { Users.id.toString() }
        userId.forEach { userId ->
            Reservations
                .deleteWhere { Reservations.user_id eq userId}
            Users
                .deleteWhere { Users.id eq userId }
        }
    }

    override suspend fun getUserByUsername(username: String): User? = dbQuery {
        Users
            .select { Users.username eq username }
            .map {
                User(
                    id = it[Users.id],
                    username = it[Users.username],
                    role = it[Users.role],
                    dinningHall = it[Users.dinningHall],
                    last = it[Users.last],
                    active = it[Users.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun getUserById(userId: String): User? = dbQuery {
        Users
            .select { Users.id eq userId }
            .map {
                User(
                    id = it[Users.id],
                    username = it[Users.username],
                    role = it[Users.role],
                    dinningHall = it[Users.dinningHall],
                    last = it[Users.last],
                    active = it[Users.active]
                )
            }
            .singleOrNull()
    }



    override suspend fun getMealsForTheNextDays(user: User): List<Meal> = dbQuery{
        val today = LocalDate.now().toString()
        return@dbQuery Schedule
            .select { ((Schedule.date greaterEq today) and (Schedule.dinningHall eq user.dinningHall))}
            .map {
                Meal(
                    id = it[Schedule.id],
                    date = it[Schedule.date],
                    time = it[Schedule.time],
                    dinningHall = it[Schedule.dinningHall],
                    active = it[Schedule.active]
                )
            }
    }

    override suspend fun getMealById(mealId: String): Meal? = dbQuery {
        Schedule
            .select { Schedule.id eq mealId }
            .map {
                Meal(
                    id = it[Schedule.id],
                    date = it[Schedule.date],
                    time = it[Schedule.time],
                    dinningHall = it[Schedule.dinningHall],
                    active = it[Schedule.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun getMeal(meal: Meal): Meal? = dbQuery {
        Schedule
            .select { ((Schedule.date eq meal.date) and (Schedule.time eq meal.time) and (Schedule.dinningHall eq meal.dinningHall)) }
            .map {
                Meal(
                    id = it[Schedule.id],
                    date = it[Schedule.date],
                    time = it[Schedule.time],
                    dinningHall = it[Schedule.dinningHall],
                    active = it[Schedule.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun insertMeals(meal: Meal): Unit = dbQuery {
        Schedule
            .insert {
                it[id] = meal.id
                it[date] = meal.date
                it[time] = meal.time
                it[dinningHall] = meal.dinningHall
                it[active] = meal.active
            }
    }

    override suspend fun activateMeals(days: Long): Unit = dbQuery {
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
                it[user_id] = reservation.userId
                it[meal_id] = reservation.mealId
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
            .select { Reservations.user_id eq userId }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    userId = it[Reservations.user_id],
                    mealId = it[Reservations.meal_id],
                    dateOfReservation = it[Reservations.date_of_reservation],
                )
            }
    }

    override suspend fun getReservationByMealIdAndUserId(mealId: String, userId: String): Reservation? = dbQuery {
        Reservations
            .select { ((Reservations.meal_id eq mealId) and (Reservations.user_id eq userId)) }
            .map {
                Reservation(
                    id = it[Reservations.id],
                    userId = it[Reservations.user_id],
                    mealId = it[Reservations.meal_id],
                    dateOfReservation = it[Reservations.date_of_reservation],
                )
            }
            .singleOrNull()
    }



}