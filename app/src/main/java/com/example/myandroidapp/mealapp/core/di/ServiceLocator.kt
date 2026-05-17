package com.example.myandroidapp.mealapp.core.di

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.myandroidapp.mealapp.core.buildconfig.impl.BuildConfigProviderImpl
import com.example.myandroidapp.mealapp.core.data.cache.MealCacheRepository
import com.example.myandroidapp.mealapp.core.data.mapper.MealMapper
import com.example.myandroidapp.mealapp.core.data.repository.MealRepositoryImpl
import com.example.myandroidapp.mealapp.core.domain.repository.MealRepository
import com.example.myandroidapp.mealapp.core.network.MealDbApi
import com.example.myandroidapp.mealapp.core.utils.handler.GeneralExceptionHandler
import com.example.myandroidapp.mealapp.core.utils.handler.GeneralExceptionHandlerImpl
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

object ServiceLocator {

    private val buildConfigProviderImpl = BuildConfigProviderImpl()

    private val okHttpClient = OkHttpClient.Builder()
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(buildConfigProviderImpl.getMealDbApiBaseUrl())
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val mealDbApi = retrofit.create(MealDbApi::class.java)

    private val gson = Gson()
    private var cacheRepository: MealCacheRepository? = null
    private var mealRepository: MealRepositoryImpl? = null

    fun getMealDbApi(): MealDbApi = mealDbApi
    fun getBuildConfigProvider() = buildConfigProviderImpl

    fun getMealRepository(context: Context): MealRepositoryImpl {
        if (mealRepository == null) {
            if (cacheRepository == null) {
                cacheRepository = MealCacheRepository(context, gson)
            }
            mealRepository = MealRepositoryImpl(
                mealDbApi = getMealDbApi(),
                mealMapper = MealMapper(),
                cacheRepository = cacheRepository!!,
                context = context
            )
        }
        return mealRepository!!
    }

    fun setHardcodedErrorEnabled(enabled: Boolean) {
        mealRepository?.enableHardcodedError = enabled
    }

    fun getGeneralExceptionHandler(): GeneralExceptionHandler {
        return GeneralExceptionHandlerImpl()
    }

    fun clearCache() {
        mealRepository = null
        cacheRepository = null
    }
}