package com.siral.data

import com.siral.data.admin.Admin
import com.siral.data.admin.AdminDataSource
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
): AdminDataSource, UserDataSource, MealDataSource, ReservationDataSource, EventsDataSource {

    object Admins: Table() {
        val username = varchar("username", 50)
        override val primaryKey = PrimaryKey(username)
    }

    object Users: Table() {
        val id = varchar("id", 128)
        val username = varchar("username", 50)
        val role = varchar("role", 20)
        val dinningHall = varchar("dinning_hall", 100)
        val status = bool("status")
        override val primaryKey = PrimaryKey(id)
    }

    object Meals: Table(){
        val id = varchar("id", 128)
        val date = varchar("date", 16)
        val time = varchar("time", 16)
        val dinningHall = varchar("dinning_hall", 32)
        val active = bool("active")

        override val primaryKey = PrimaryKey(id)
    }

    object Reservations: Table() {
        val id = varchar("id", 128)
        val user_id = varchar("user_id", 128) references Users.id
        val meal_id = varchar("meal_id", 128) references Meals.id
        val date_of_reservation = varchar("date_of_reservation", 16)
        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Admins, Users, Meals, Reservations)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }


    /**
     * Events Data Source
     */
    override suspend fun activateMeals(days: Long): Unit = dbQuery {
        val date = LocalDate.now().plusDays(days).toString()
        Meals
            .update({ Meals.date eq date }){
                it[active] = true
            }
    }

    /**
     * AdminDataSource
     */
    override suspend fun insertAdmin(userName: String): Unit = dbQuery {
        Admins
            .insert {
                it[username] = userName
            }
    }

//    TODO("Find the way to make this method private")
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


    /**
     * UserDataSource
     */
    override suspend fun insertUser(user: User): Unit = dbQuery {
        Users
            .insert{
                it[id] = user.id
                it[username] = user.username
                it[role] = user.role
                it[dinningHall] = user.dinningHall
                it[status] = user.status
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
                    status = it[Users.status]
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
                    status = it[Users.status]
                )
            }
            .singleOrNull()
    }

    /**
     * MealDataSource
     */
    override suspend fun getMealsForTheNextDays(user: User): List<Meal> = dbQuery{
        val today = LocalDate.now().toString()
        return@dbQuery Meals
            .select { ((Meals.date greaterEq today) and (Meals.dinningHall eq user.dinningHall))}
            .map {
                Meal(
                    id = it[Meals.id],
                    date = it[Meals.date],
                    time = it[Meals.time],
                    dinningHall = it[Meals.dinningHall],
                    active = it[Meals.active]
                )
            }
    }

    override suspend fun getMealById(mealId: String): Meal? = dbQuery {
        Meals
            .select { Meals.id eq mealId }
            .map {
                Meal(
                    id = it[Meals.id],
                    date = it[Meals.date],
                    time = it[Meals.time],
                    dinningHall = it[Meals.dinningHall],
                    active = it[Meals.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun getMeal(meal: Meal): Meal? = dbQuery {
        Meals
            .select { ((Meals.date eq meal.date) and (Meals.time eq meal.time) and (Meals.dinningHall eq meal.dinningHall)) }
            .map {
                Meal(
                    id = it[Meals.id],
                    date = it[Meals.date],
                    time = it[Meals.time],
                    dinningHall = it[Meals.dinningHall],
                    active = it[Meals.active]
                )
            }
            .singleOrNull()
    }

    override suspend fun insertMeals(meal: Meal): Unit = dbQuery {
        Meals
            .insert {
                it[id] = meal.id
                it[date] = meal.date
                it[time] = meal.time
                it[dinningHall] = meal.dinningHall
                it[active] = meal.active
            }
    }

    /**
     * ReservationDataSource
     */
    override suspend fun insertReservation(reservation: Reservation): Unit = dbQuery {
        Reservations
            .insert {
                it[id] = reservation.id
                it[user_id] = reservation.userId
                it[meal_id] = reservation.mealId
                it[date_of_reservation] = reservation.dateOfReservation
            }
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

    override suspend fun getReservationByMealdId(mealId: String): Reservation? = dbQuery {
        Reservations
            .select { Reservations.meal_id eq mealId }
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