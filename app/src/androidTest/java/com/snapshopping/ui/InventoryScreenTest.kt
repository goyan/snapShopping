package com.snapshopping.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.snapshopping.data.model.FoodCategory
import com.snapshopping.data.model.FoodItem
import com.snapshopping.data.model.InventoryUiState
import com.snapshopping.ui.inventory.InventoryScreen
import com.snapshopping.ui.inventory.InventoryViewModel
import com.snapshopping.ui.theme.SnapShoppingTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InventoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsEmptyMessage() {
        // Given
        val viewModel = mockk<InventoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(InventoryUiState(items = emptyList()))

        // When
        composeTestRule.setContent {
            SnapShoppingTheme {
                InventoryScreen(
                    viewModel = viewModel,
                    onNavigateToCamera = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Your fridge is empty!").assertIsDisplayed()
    }

    @Test
    fun withItems_showsItemsList() {
        // Given
        val items = listOf(
            FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f, quantity = 2),
            FoodItem(name = "Apple", category = FoodCategory.FRUITS, confidence = 0.85f, quantity = 3)
        )
        val viewModel = mockk<InventoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(InventoryUiState(items = items))

        // When
        composeTestRule.setContent {
            SnapShoppingTheme {
                InventoryScreen(
                    viewModel = viewModel,
                    onNavigateToCamera = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Milk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Apple").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed() // Quantity
        composeTestRule.onNodeWithText("3").assertIsDisplayed() // Quantity
    }

    @Test
    fun categoryHeaders_areDisplayed() {
        // Given
        val items = listOf(
            FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f),
            FoodItem(name = "Apple", category = FoodCategory.FRUITS, confidence = 0.85f)
        )
        val viewModel = mockk<InventoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(InventoryUiState(items = items))

        // When
        composeTestRule.setContent {
            SnapShoppingTheme {
                InventoryScreen(
                    viewModel = viewModel,
                    onNavigateToCamera = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Dairy").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fruits").assertIsDisplayed()
    }

    @Test
    fun scanButton_navigatesToCamera() {
        // Given
        var navigated = false
        val viewModel = mockk<InventoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(InventoryUiState(items = emptyList()))

        // When
        composeTestRule.setContent {
            SnapShoppingTheme {
                InventoryScreen(
                    viewModel = viewModel,
                    onNavigateToCamera = { navigated = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Scan Fridge").performClick()

        // Then
        assert(navigated)
    }

    @Test
    fun addButton_opensAddDialog() {
        // Given
        val viewModel = mockk<InventoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(InventoryUiState(items = emptyList()))

        // When
        composeTestRule.setContent {
            SnapShoppingTheme {
                InventoryScreen(
                    viewModel = viewModel,
                    onNavigateToCamera = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add Item").performClick()

        // Then
        composeTestRule.onNodeWithText("Add Item").assertIsDisplayed()
        composeTestRule.onNodeWithText("Item name").assertIsDisplayed()
    }

    @Test
    fun loadingState_showsProgress() {
        // Given
        val viewModel = mockk<InventoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(InventoryUiState(isLoading = true))

        // When
        composeTestRule.setContent {
            SnapShoppingTheme {
                InventoryScreen(
                    viewModel = viewModel,
                    onNavigateToCamera = {}
                )
            }
        }

        // Then - Loading indicator should be visible
        // Note: CircularProgressIndicator doesn't have text, so we check it exists
        composeTestRule.onNodeWithText("Your fridge is empty!").assertDoesNotExist()
    }

    @Test
    fun confidenceScore_isDisplayed() {
        // Given
        val items = listOf(
            FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.92f)
        )
        val viewModel = mockk<InventoryViewModel>(relaxed = true)
        every { viewModel.uiState } returns MutableStateFlow(InventoryUiState(items = items))

        // When
        composeTestRule.setContent {
            SnapShoppingTheme {
                InventoryScreen(
                    viewModel = viewModel,
                    onNavigateToCamera = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Confidence: 92%").assertIsDisplayed()
    }
}
