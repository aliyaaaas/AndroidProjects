package com.example.myandroidapp.mealapp.core.network

import com.example.myandroidapp.mealapp.core.network.pojo.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealDbApi {

    @GET("search.php")
    suspend fun searchMeals(
        @Query("s") query: String
    ): MealResponse

    @GET("lookup.php")
    suspend fun getMealDetails(
        @Query("i") id: String
    ): MealResponse
}