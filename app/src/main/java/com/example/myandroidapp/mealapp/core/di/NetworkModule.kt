package com.example.myandroidapp.mealapp.core.di

import com.example.myandroidapp.mealapp.core.buildconfig.api.BuildConfigProvider
import com.example.myandroidapp.mealapp.core.buildconfig.impl.BuildConfigProviderImpl
import com.example.myandroidapp.mealapp.core.network.MealDbApi
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideBuildConfigProvider(): BuildConfigProvider =
        BuildConfigProviderImpl()

    @Provides
    @Singleton
    fun provideOkHttpClient():
            OkHttpClient = OkHttpClient.Builder()
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, buildConfigProvider: BuildConfigProvider):
            Retrofit = Retrofit.Builder()
                .baseUrl(buildConfigProvider.getMealDbApiBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    @Provides
    @Singleton
    fun provideMealDbApi(retrofit: Retrofit):
            MealDbApi = retrofit.create(MealDbApi::class.java)
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}