package com.example.myandroidapp.mealapp.core.di

import com.example.myandroidapp.mealapp.core.domain.repository.MealRepository
import com.example.myandroidapp.mealapp.core.domain.usecase.GetMealDetailsUseCase
import com.example.myandroidapp.mealapp.core.domain.usecase.SearchMealByQueryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    @ViewModelScoped
    fun provideSearchMealByQueryUseCase(repo: MealRepository):
            SearchMealByQueryUseCase = SearchMealByQueryUseCase(repo)

    @Provides
    @ViewModelScoped
    fun provideGetMealDetailsUseCase(repo: MealRepository):
            GetMealDetailsUseCase = GetMealDetailsUseCase(repo)
}