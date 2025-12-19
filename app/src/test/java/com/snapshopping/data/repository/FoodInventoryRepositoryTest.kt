package com.snapshopping.data.repository

import android.graphics.Bitmap
import com.google.common.truth.Truth.assertThat
import com.snapshopping.data.local.FoodItemDao
import com.snapshopping.data.model.DetectedFood
import com.snapshopping.data.model.FoodCategory
import com.snapshopping.data.model.FoodItem
import com.snapshopping.data.remote.VisionApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FoodInventoryRepositoryTest {

    private lateinit var repository: FoodInventoryRepositoryImpl
    private lateinit var mockDao: FoodItemDao
    private lateinit var mockVisionService: VisionApiService

    @Before
    fun setup() {
        mockDao = mockk(relaxed = true)
        mockVisionService = mockk()
        repository = FoodInventoryRepositoryImpl(mockDao, mockVisionService)
    }

    @Test
    fun `getAllItems returns flow from dao`() = runTest {
        // Given
        val items = listOf(
            FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f),
            FoodItem(name = "Apple", category = FoodCategory.FRUITS, confidence = 0.85f)
        )
        every { mockDao.getAllItems() } returns flowOf(items)

        // When
        val result = repository.getAllItems()

        // Then
        result.collect { resultItems ->
            assertThat(resultItems).hasSize(2)
            assertThat(resultItems[0].name).isEqualTo("Milk")
        }
    }

    @Test
    fun `addItem calls dao insertItem`() = runTest {
        // Given
        val item = FoodItem(name = "Cheese", category = FoodCategory.DAIRY, confidence = 0.95f)

        // When
        repository.addItem(item)

        // Then
        coVerify { mockDao.insertItem(item) }
    }

    @Test
    fun `deleteItem calls dao deleteItem`() = runTest {
        // Given
        val item = FoodItem(name = "Cheese", category = FoodCategory.DAIRY, confidence = 0.95f)

        // When
        repository.deleteItem(item)

        // Then
        coVerify { mockDao.deleteItem(item) }
    }

    @Test
    fun `analyzeImages filters low confidence items`() = runTest {
        // Given
        val mockBitmap = mockk<Bitmap>()
        val detectedFoods = listOf(
            DetectedFood(name = "milk", category = "dairy", confidence = 0.9f),
            DetectedFood(name = "unknown", category = "other", confidence = 0.3f), // Low confidence
            DetectedFood(name = "apple", category = "fruits", confidence = 0.8f)
        )
        coEvery { mockVisionService.analyzeImages(any()) } returns Result.success(detectedFoods)

        // When
        val result = repository.analyzeImages(listOf(mockBitmap))

        // Then
        assertThat(result.isSuccess).isTrue()
        val items = result.getOrNull()!!
        assertThat(items).hasSize(2)
        assertThat(items.map { it.name }).doesNotContain("Unknown")
    }

    @Test
    fun `analyzeImages normalizes plural names to singular`() = runTest {
        // Given
        val mockBitmap = mockk<Bitmap>()
        val detectedFoods = listOf(
            DetectedFood(name = "apples", category = "fruits", confidence = 0.9f),
            DetectedFood(name = "tomatoes", category = "vegetables", confidence = 0.85f),
            DetectedFood(name = "berries", category = "fruits", confidence = 0.8f)
        )
        coEvery { mockVisionService.analyzeImages(any()) } returns Result.success(detectedFoods)

        // When
        val result = repository.analyzeImages(listOf(mockBitmap))

        // Then
        assertThat(result.isSuccess).isTrue()
        val items = result.getOrNull()!!
        assertThat(items.map { it.name }).containsExactly("Apple", "Tomato", "Berry")
    }

    @Test
    fun `analyzeImages merges duplicate items`() = runTest {
        // Given
        val mockBitmap = mockk<Bitmap>()
        val detectedFoods = listOf(
            DetectedFood(name = "milk", category = "dairy", confidence = 0.9f),
            DetectedFood(name = "milk", category = "dairy", confidence = 0.85f),
            DetectedFood(name = "milk", category = "dairy", confidence = 0.8f)
        )
        coEvery { mockVisionService.analyzeImages(any()) } returns Result.success(detectedFoods)

        // When
        val result = repository.analyzeImages(listOf(mockBitmap))

        // Then
        assertThat(result.isSuccess).isTrue()
        val items = result.getOrNull()!!
        assertThat(items).hasSize(1)
        assertThat(items[0].name).isEqualTo("Milk")
        assertThat(items[0].quantity).isEqualTo(3) // Merged quantity
        assertThat(items[0].confidence).isEqualTo(0.9f) // Highest confidence
    }

    @Test
    fun `analyzeImages preserves exception words like cheese`() = runTest {
        // Given
        val mockBitmap = mockk<Bitmap>()
        val detectedFoods = listOf(
            DetectedFood(name = "cheese", category = "dairy", confidence = 0.9f),
            DetectedFood(name = "lettuce", category = "vegetables", confidence = 0.85f),
            DetectedFood(name = "rice", category = "other", confidence = 0.8f)
        )
        coEvery { mockVisionService.analyzeImages(any()) } returns Result.success(detectedFoods)

        // When
        val result = repository.analyzeImages(listOf(mockBitmap))

        // Then
        assertThat(result.isSuccess).isTrue()
        val items = result.getOrNull()!!
        assertThat(items.map { it.name }).containsExactly("Cheese", "Lettuce", "Rice")
    }

    @Test
    fun `analyzeImages returns failure when vision service fails`() = runTest {
        // Given
        val mockBitmap = mockk<Bitmap>()
        val error = Exception("API Error")
        coEvery { mockVisionService.analyzeImages(any()) } returns Result.failure(error)

        // When
        val result = repository.analyzeImages(listOf(mockBitmap))

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).isEqualTo("API Error")
    }

    @Test
    fun `analyzeImages maps categories correctly`() = runTest {
        // Given
        val mockBitmap = mockk<Bitmap>()
        val detectedFoods = listOf(
            DetectedFood(name = "milk", category = "dairy", confidence = 0.9f),
            DetectedFood(name = "steak", category = "meat", confidence = 0.85f),
            DetectedFood(name = "carrot", category = "vegetables", confidence = 0.8f),
            DetectedFood(name = "cola", category = "beverages", confidence = 0.75f),
            DetectedFood(name = "unknown", category = "invalid_category", confidence = 0.7f)
        )
        coEvery { mockVisionService.analyzeImages(any()) } returns Result.success(detectedFoods)

        // When
        val result = repository.analyzeImages(listOf(mockBitmap))

        // Then
        assertThat(result.isSuccess).isTrue()
        val items = result.getOrNull()!!
        assertThat(items.find { it.name == "Milk" }?.category).isEqualTo(FoodCategory.DAIRY)
        assertThat(items.find { it.name == "Steak" }?.category).isEqualTo(FoodCategory.MEAT)
        assertThat(items.find { it.name == "Carrot" }?.category).isEqualTo(FoodCategory.VEGETABLES)
        assertThat(items.find { it.name == "Cola" }?.category).isEqualTo(FoodCategory.BEVERAGES)
        assertThat(items.find { it.name == "Unknown" }?.category).isEqualTo(FoodCategory.OTHER)
    }
}
