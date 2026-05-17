package com.example.myandroidapp.mealapp

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.myandroidapp.R
import com.example.myandroidapp.mealapp.core.domain.model.MealModel
import com.example.myandroidapp.mealapp.core.domain.usecase.SearchMealByQueryUseCase
import com.example.myandroidapp.mealapp.core.di.ServiceLocator
import com.example.myandroidapp.mealapp.core.utils.handler.GeneralExceptionHandler
import com.example.myandroidapp.mealapp.core.utils.runCatching
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class MainActivityViewModel(
    private val generalExceptionHandler: GeneralExceptionHandler,
    private val searchMealByQueryUseCase: SearchMealByQueryUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val applicationContext: Context,
) : ViewModel() {

    private val sharedPreferences: SharedPreferences =
        applicationContext.getSharedPreferences(
            applicationContext.getString(R.string.pref_name),
            Context.MODE_PRIVATE
        )
    private val gson = Gson()

    private val _mealList = MutableStateFlow<List<MealModel>>(emptyList())
    val mealList: StateFlow<List<MealModel>> = _mealList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _lastQuery = MutableStateFlow("")
    val lastQuery: StateFlow<String> = _lastQuery.asStateFlow()

    init {
        val savedQuery = sharedPreferences.getString(
            applicationContext.getString(R.string.pref_key_last_query),
            null
        )
        val savedMealsJson = sharedPreferences.getString(
            applicationContext.getString(R.string.pref_key_meal_list),
            null
        )

        if (savedQuery != null && savedMealsJson != null) {
            _lastQuery.value = savedQuery
            val type = object : TypeToken<List<MealModel>>() {}.type
            val savedMeals = gson.fromJson<List<MealModel>>(savedMealsJson, type)
            if (savedMeals != null && savedMeals.isNotEmpty()) {
                _mealList.value = savedMeals
            }
        }

        savedStateHandle.get<String>("lastQuery")?.let { query ->
            if (query.isNotBlank() && _mealList.value.isEmpty()) {
                _lastQuery.value = query
                searchMeals(query)
            }
        }

        viewModelScope.launch {
            val repository = ServiceLocator.getMealRepository(applicationContext)
            repository.dataSourceEvent.collect { message ->
                _snackbarMessage.value = message
            }
        }
    }

    fun searchMeals(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            runCatching(generalExceptionHandler) {
                searchMealByQueryUseCase(query)
            }.onSuccess { meals ->
                _mealList.value = meals
                savedStateHandle["lastQuery"] = query
                savedStateHandle["mealList"] = meals

                sharedPreferences.edit()
                    .putString(applicationContext.getString(R.string.pref_key_last_query), query)
                    .putString(applicationContext.getString(R.string.pref_key_meal_list), gson.toJson(meals))
                    .apply()
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: applicationContext.getString(R.string.error_unknown)
            }

            _isLoading.value = false
        }
    }

    fun getLastQuery(): String = _lastQuery.value

    fun onSnackbarShown() {
        _snackbarMessage.value = null
    }

    fun setHardcodedErrorEnabled(enabled: Boolean) {
        ServiceLocator.setHardcodedErrorEnabled(enabled)
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
                val useCase = SearchMealByQueryUseCase(
                    mealRepository = ServiceLocator.getMealRepository(context)
                )

                return MainActivityViewModel(
                    generalExceptionHandler = handler,
                    searchMealByQueryUseCase = useCase,
                    savedStateHandle = extras.createSavedStateHandle(),
                    applicationContext = context
                ) as T
            }
        }
    }
}