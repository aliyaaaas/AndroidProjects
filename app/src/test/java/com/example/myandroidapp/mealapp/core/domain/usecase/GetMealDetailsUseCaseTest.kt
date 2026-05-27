package com.example.myandroidapp.mealapp.core.domain.usecase

import com.example.myandroidapp.mealapp.core.domain.model.MealModel
import com.example.myandroidapp.mealapp.core.domain.repository.MealRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetMealDetailsUseCaseTest {

    private lateinit var repo: MealRepository
    private lateinit var useCase: GetMealDetailsUseCase

    @Before
    fun setup() {
        repo = mockk()
        useCase = GetMealDetailsUseCase(repo)
    }

    @Test
    fun `получение деталей блюда по валидному id`() = runTest {
        val id = "52772"
        val expected = MealModel(
            id = id,
            name = "Chicken Soup",
            category = "Soup",
            area = "Russian",
            instructions = "Boil chicken, add vegetables",
            imageUrl = "https://example.com/soup.jpg",
            tags = "soup,chicken",
            youtubeUrl = "https://youtube.com/watch?v=abc"
        )
        coEvery { repo.getMealDetails(id) } returns expected

        val result = useCase(id)

        assertEquals(expected, result)
        assertEquals(id, result?.id)
        coVerify(exactly = 1) { repo.getMealDetails(id) }
    }

    @Test
    fun `возврат null при несуществующем id`() = runTest {
        coEvery { repo.getMealDetails("invalid") } returns null

        val result = useCase("invalid")

        assertNull(result)
        coVerify(exactly = 1) { repo.getMealDetails("invalid") }
    }
}