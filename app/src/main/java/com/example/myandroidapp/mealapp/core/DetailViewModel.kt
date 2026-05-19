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
import com.example.myandroidapp.mealapp.core.utils.Constants
import com.example.myandroidapp.mealapp.core.utils.handler.GeneralExceptionHandler
import com.example.myandroidapp.mealapp.core.utils.runCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class DetailViewModel @Inject constructor(
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

    fun loadMealDetails() {
        val mealId = savedStateHandle.get<String>(Constants.KEY_MEAL_ID) ?: run {
            _errorMessage.value = "Meal ID is missing"
            return
        }
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

}