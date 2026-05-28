package com.example.myandroidapp.mealapp.core.data.repository

import android.content.Context
import com.example.myandroidapp.R
import com.example.myandroidapp.mealapp.core.data.cache.MealCacheRepository
import com.example.myandroidapp.mealapp.core.data.mapper.MealMapper
import com.example.myandroidapp.mealapp.core.domain.model.MealModel
import com.example.myandroidapp.mealapp.core.domain.repository.MealRepository
import com.example.myandroidapp.mealapp.core.network.MealDbApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException

class MealRepositoryImpl(
    private val mealDbApi: MealDbApi,
    private val mealMapper: MealMapper,
    private val cacheRepository: MealCacheRepository?,
    private val context: Context
) : MealRepository {

    private val _dataSourceEvent = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val dataSourceEvent: SharedFlow<String> = _dataSourceEvent.asSharedFlow()

    private var requestCounter = 0
    var enableHardcodedError = true

    override suspend fun searchByQuery(query: String): List<MealModel> {
        if (cacheRepository != null) {
            val cachedMeals = cacheRepository.getCachedMeals(query)
            if (cachedMeals != null) {
                _dataSourceEvent.emit(
                    context.getString(R.string.data_from_cache, MealCacheRepository.CACHE_TTL_SECONDS)
                )
                return cachedMeals
            }
        }

        requestCounter++
        if (enableHardcodedError && requestCounter % 3 == 0) {
            _dataSourceEvent.emit(context.getString(R.string.error_404_demo))
            throw HttpException(
                retrofit2.Response.error<List<MealModel>>(
                    404,
                    "Demo 404 Error".toResponseBody(null)
                )
            )
        }

        val response = mealDbApi.searchMeals(query)
        val meals = response.meals ?: emptyList()
        val domainMeals = mealMapper.mapToDomainList(meals)

        if (cacheRepository != null && domainMeals.isNotEmpty()) {
            cacheRepository.saveToCache(query, domainMeals)
            _dataSourceEvent.emit(
                context.getString(R.string.data_from_server_cached, MealCacheRepository.CACHE_TTL_SECONDS)
            )
        } else {
            _dataSourceEvent.emit(context.getString(R.string.data_from_server))
        }

        return domainMeals
    }

    override suspend fun getMealDetails(mealId: String): MealModel? {
        val response = mealDbApi.getMealDetails(mealId)
        val meal = response.meals?.firstOrNull()
        return mealMapper.mapToDomain(meal)
    }
}