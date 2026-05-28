package com.example.myandroidapp.mealapp.core.domain.usecase

import com.example.myandroidapp.mealapp.core.domain.model.MealModel
import com.example.myandroidapp.mealapp.core.domain.repository.MealRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchMealByQueryUseCaseTest {

    private lateinit var repo: MealRepository
    private lateinit var useCase: SearchMealByQueryUseCase

    @Before
    fun setup() {
        repo = mockk()
        useCase = SearchMealByQueryUseCase(repo)
    }

    @Test
    fun `поиск возвращает список при валидном запросе`() = runTest {
        val query = "pasta"
        val expected = listOf(
            MealModel("1", "Pasta", "Main", "Italian", "Cook pasta", "img.jpg", null, null)
        )
        coEvery { repo.searchByQuery(query) } returns expected

        val result = useCase(query)

        assertEquals(expected, result)
        coVerify(exactly = 1) { repo.searchByQuery(query) }
    }

    @Test
    fun `поиск возвращает пустой список при пустом запросе`() = runTest {
        coEvery { repo.searchByQuery("") } returns emptyList()

        val result = useCase("")

        assertTrue(result.isEmpty())
        coVerify(exactly = 1) { repo.searchByQuery("") }
    }
}