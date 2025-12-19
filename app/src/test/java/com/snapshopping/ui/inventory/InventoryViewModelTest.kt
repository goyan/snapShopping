package com.snapshopping.ui.inventory

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.snapshopping.data.model.FoodCategory
import com.snapshopping.data.model.FoodItem
import com.snapshopping.data.repository.FoodInventoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {

    private lateinit var viewModel: InventoryViewModel
    private lateinit var mockRepository: FoodInventoryRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
        every { mockRepository.getAllItems() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = InventoryViewModel(mockRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun `initial state has isLoading true`() = runTest {
        // Given
        every { mockRepository.getAllItems() } returns flowOf(emptyList())

        // When
        viewModel = InventoryViewModel(mockRepository)

        // Then - Before loading completes
        assertThat(viewModel.uiState.value.isLoading).isTrue()
    }

    @Test
    fun `state updates with items from repository`() = runTest {
        // Given
        val items = listOf(
            FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f),
            FoodItem(name = "Apple", category = FoodCategory.FRUITS, confidence = 0.85f)
        )
        every { mockRepository.getAllItems() } returns flowOf(items)

        // When
        createViewModel()

        // Then
        assertThat(viewModel.uiState.value.items).hasSize(2)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `addItem calls repository with correct item`() = runTest {
        // Given
        createViewModel()

        // When
        viewModel.addItem("Cheese", FoodCategory.DAIRY, 2)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            mockRepository.addItem(match {
                it.name == "Cheese" &&
                it.category == FoodCategory.DAIRY &&
                it.quantity == 2 &&
                it.confidence == 1.0f
            })
        }
    }

    @Test
    fun `updateItem calls repository`() = runTest {
        // Given
        createViewModel()
        val item = FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f)

        // When
        viewModel.updateItem(item)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { mockRepository.updateItem(item) }
    }

    @Test
    fun `updateQuantity increases quantity`() = runTest {
        // Given
        createViewModel()
        val item = FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f, quantity = 1)

        // When
        viewModel.updateQuantity(item, 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            mockRepository.updateItem(match { it.quantity == 2 })
        }
    }

    @Test
    fun `updateQuantity decreases quantity`() = runTest {
        // Given
        createViewModel()
        val item = FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f, quantity = 3)

        // When
        viewModel.updateQuantity(item, -1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            mockRepository.updateItem(match { it.quantity == 2 })
        }
    }

    @Test
    fun `updateQuantity does not go below 1`() = runTest {
        // Given
        createViewModel()
        val item = FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f, quantity = 1)

        // When
        viewModel.updateQuantity(item, -5)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            mockRepository.updateItem(match { it.quantity == 1 })
        }
    }

    @Test
    fun `deleteItem calls repository`() = runTest {
        // Given
        createViewModel()
        val item = FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f)

        // When
        viewModel.deleteItem(item)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { mockRepository.deleteItem(item) }
    }

    @Test
    fun `clearInventory calls repository`() = runTest {
        // Given
        createViewModel()

        // When
        viewModel.clearInventory()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { mockRepository.clearInventory() }
    }

    @Test
    fun `error during addItem sets error message`() = runTest {
        // Given
        createViewModel()
        coEvery { mockRepository.addItem(any()) } throws Exception("Database error")

        // When
        viewModel.addItem("Cheese", FoodCategory.DAIRY, 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.uiState.value.errorMessage).contains("Database error")
    }

    @Test
    fun `clearError removes error message`() = runTest {
        // Given
        createViewModel()
        coEvery { mockRepository.addItem(any()) } throws Exception("Error")
        viewModel.addItem("Test", FoodCategory.OTHER, 1)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value.errorMessage).isNotNull()

        // When
        viewModel.clearError()

        // Then
        assertThat(viewModel.uiState.value.errorMessage).isNull()
    }
}
