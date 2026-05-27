package com.example.myandroidapp.mealapp

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.myandroidapp.mealapp.core.domain.model.MealModel
import com.example.myandroidapp.mealapp.core.domain.repository.MealRepository
import com.example.myandroidapp.mealapp.core.domain.usecase.SearchMealByQueryUseCase
import com.example.myandroidapp.mealapp.core.utils.handler.GeneralExceptionHandler
import com.example.myandroidapp.R
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var vm: MainActivityViewModel
    private lateinit var handler: GeneralExceptionHandler
    private lateinit var useCase: SearchMealByQueryUseCase
    private lateinit var repo: MealRepository
    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var savedState: SavedStateHandle

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        handler = mockk(relaxed = true)
        useCase = mockk()
        repo = mockk()
        context = mockk()
        prefs = mockk()
        editor = mockk()
        savedState = mockk()

        every { context.getString(R.string.pref_name) } returns "app_state"
        every { context.getString(R.string.pref_key_last_query) } returns "lastQuery"
        every { context.getString(R.string.pref_key_meal_list) } returns "mealList"
        every { context.getString(R.string.pref_key_info_screen_seen) } returns "info_screen_seen"
        every { context.getString(R.string.pref_key_user_id) } returns "user_id"

        every { context.getSharedPreferences(any(), any()) } returns prefs
        every { prefs.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.putBoolean(any(), any()) } returns editor
        every { editor.apply() } returns Unit
        every { prefs.getBoolean(any(), any()) } returns false
        every { prefs.getString(any(), any()) } returns null

        every { repo.dataSourceEvent } returns MutableSharedFlow<String>().asSharedFlow()

        vm = MainActivityViewModel(handler, useCase, savedState, context, repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `поиск обновляет список при успехе`() = runTest {
        val query = "soup"
        val meals = listOf(MealModel("1", "Soup", "Main", "RU", "Boil water", "img.jpg", null, null))
        coEvery { useCase(query) } returns meals

        vm.searchMeals(query)
        advanceUntilIdle()

        assertEquals(meals, vm.mealList.value)
        coVerify { useCase(query) }
    }

    @Test
    fun `поиск показывает пустой список при отсутствии результатов`() = runTest {
        coEvery { useCase("xyz") } returns emptyList()

        vm.searchMeals("xyz")
        advanceUntilIdle()

        assertTrue(vm.mealList.value.isEmpty())
    }

    @Test
    fun `поиск обрабатывает ошибку`() = runTest {
        coEvery { useCase("error") } throws Exception("Network error")

        vm.searchMeals("error")
        advanceUntilIdle()

        assertEquals("Network error", vm.errorMessage.value)
    }

    @Test
    fun `поиск не выполняется при пустом запросе`() = runTest {
        vm.searchMeals("")
        advanceUntilIdle()

        coVerify(exactly = 0) { useCase(any()) }
    }

    @Test
    fun `скрытие инфо-экрана обновляет настройки`() = runTest {
        vm.markInfoScreenAsSeen()
        advanceUntilIdle()

        coVerify { editor.putBoolean(any(), true) }
        assertFalse(vm.shouldShowInfoScreen.value)
    }

    @Test
    fun `очистка Snackbar сообщения`() = runTest {
        vm.onSnackbarShown()
        advanceUntilIdle()

        assertNull(vm.snackbarMessage.value)
    }
}