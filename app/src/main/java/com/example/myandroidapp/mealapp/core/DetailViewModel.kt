package com.example.myandroidapp.mealapp.core

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.myandroidapp.mealapp.core.domain.model.MealModel
import com.example.myandroidapp.mealapp.core.domain.usecase.GetMealDetailsUseCase
import com.example.myandroidapp.mealapp.core.di.ServiceLocator
import com.example.myandroidapp.mealapp.core.utils.handler.GeneralExceptionHandler
import com.example.myandroidapp.mealapp.core.utils.runCatching
import kotlin.reflect.KClass

class DetailViewModel(
    private val generalExceptionHandler: GeneralExceptionHandler,
    private val getMealDetailsUseCase: GetMealDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _mealDetails = MutableStateFlow<MealModel?>(null)
    val mealDetails: StateFlow<MealModel?> = _mealDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadMealDetails(mealId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            runCatching(generalExceptionHandler) {
                getMealDetailsUseCase(mealId)
            }.onSuccess { meal ->
                _mealDetails.value = meal
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message
            }

            _isLoading.value = false
        }
    }

    companion object {
        val Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: KClass<T>,
                extras: CreationExtras
            ): T {
                val context = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context
                val handler = ServiceLocator.getGeneralExceptionHandler()
                val useCase = GetMealDetailsUseCase(
                    mealRepository = ServiceLocator.getMealRepository(context)
                )

                return DetailViewModel(
                    generalExceptionHandler = handler,
                    getMealDetailsUseCase = useCase,
                    savedStateHandle = extras.createSavedStateHandle()
                ) as T
            }
        }
    }
}