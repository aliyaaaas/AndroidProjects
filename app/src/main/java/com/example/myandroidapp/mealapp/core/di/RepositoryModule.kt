package com.example.myandroidapp.mealapp.core.di

import android.content.Context
import com.example.myandroidapp.mealapp.core.data.cache.MealCacheRepository
import com.example.myandroidapp.mealapp.core.data.mapper.MealMapper
import com.example.myandroidapp.mealapp.core.data.repository.MealRepositoryImpl
import com.example.myandroidapp.mealapp.core.domain.repository.MealRepository
import com.example.myandroidapp.mealapp.core.network.MealDbApi
import com.example.myandroidapp.mealapp.core.utils.handler.GeneralExceptionHandler
import com.example.myandroidapp.mealapp.core.utils.handler.GeneralExceptionHandlerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGeneralExceptionHandler():
            GeneralExceptionHandler = GeneralExceptionHandlerImpl()

    @Provides
    @Singleton
    fun provideMealMapper(): MealMapper = MealMapper()

    @Provides
    @Singleton
    fun provideMealCacheRepository(@ApplicationContext context: Context, gson: com.google.gson.Gson):
            MealCacheRepository = MealCacheRepository(context, gson)

    @Provides
    @Singleton
    fun provideMealRepository(mealDbApi: MealDbApi, mealMapper: MealMapper, cacheRepository: MealCacheRepository, @ApplicationContext context: Context):
            MealRepository = MealRepositoryImpl(mealDbApi, mealMapper, cacheRepository, context)
}