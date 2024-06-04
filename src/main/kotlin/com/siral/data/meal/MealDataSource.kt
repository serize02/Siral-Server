package com.siral.data.meal

import com.siral.data.user.User

interface MealDataSource {
    suspend fun getMealsForTheNextDays(user: User): List<Meal>
    suspend fun getMealById(mealId: String): Meal?
    suspend fun getMeal(meal: Meal): Meal?
    suspend fun insertMeals(meal: Meal)
    suspend fun activateMeals(days: Long)
}