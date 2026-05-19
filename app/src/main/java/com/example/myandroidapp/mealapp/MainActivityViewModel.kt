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
import com.example.myandroidapp.mealapp.core.domain.repository.MealRepository
import com.example.myandroidapp.mealapp.core.domain.usecase.SearchMealByQueryUseCase
import com.example.myandroidapp.mealapp.core.utils.handler.GeneralExceptionHandler
import com.example.myandroidapp.mealapp.core.utils.runCatching
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val generalExceptionHandler: GeneralExceptionHandler,
    private val searchMealByQueryUseCase: SearchMealByQueryUseCase,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext
    private val applicationContext: Context,
    private val mealRepository: MealRepository
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

    private val _shouldShowInfoScreen = MutableStateFlow(true)
    val shouldShowInfoScreen: StateFlow<Boolean> = _shouldShowInfoScreen.asStateFlow()

    init {
        restoreState()
        val seen = sharedPreferences.getBoolean(applicationContext.getString(R.string.pref_key_info_screen_seen), false)
        _shouldShowInfoScreen.value = !seen

        observeRepositoryEvents()
    }
    private fun observeRepositoryEvents() {
        viewModelScope.launch {
            mealRepository.dataSourceEvent.collect { message ->
                _snackbarMessage.value = message
            }
        }
    }

    private fun restoreState() {
        val savedQuery = sharedPreferences.getString(applicationContext.getString(R.string.pref_key_last_query), null)
        val savedMealsJson = sharedPreferences.getString(applicationContext.getString(R.string.pref_key_meal_list), null)
        if (savedQuery != null && savedMealsJson != null) {
            _lastQuery.value = savedQuery
            val type = object : TypeToken<List<MealModel>>() {}.type
            gson.fromJson<List<MealModel>>(savedMealsJson, type)?.let {
                if (it.isNotEmpty()) _mealList.value = it }
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
    fun markInfoScreenAsSeen() {
        sharedPreferences.edit().putBoolean(applicationContext.getString(R.string.pref_key_info_screen_seen), true).apply()
        _shouldShowInfoScreen.value = false
    }

    fun onSnackbarShown() {
        _snackbarMessage.value = null
    }
}