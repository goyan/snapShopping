package com.snapshopping.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Food item entity stored in local database
 */
@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: FoodCategory,
    val confidence: Float,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * Food category enum for classification
 */
enum class FoodCategory {
    DAIRY,
    MEAT,
    VEGETABLES,
    FRUITS,
    BEVERAGES,
    CONDIMENTS,
    LEFTOVERS,
    SNACKS,
    FROZEN,
    OTHER;

    companion object {
        fun fromString(value: String): FoodCategory {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: OTHER
        }
    }
}

/**
 * Response model from Vision API
 */
data class VisionResponse(
    val items: List<DetectedFood>
)

/**
 * Single detected food item from Vision API
 */
data class DetectedFood(
    val name: String,
    val category: String,
    val confidence: Float
)

/**
 * UI state for the scanning process
 */
sealed class ScanState {
    data object Idle : ScanState()
    data object Capturing : ScanState()
    data class Processing(val photoCount: Int) : ScanState()
    data class Success(val items: List<FoodItem>) : ScanState()
    data class Error(val message: String) : ScanState()
}

/**
 * UI state for the inventory screen
 */
data class InventoryUiState(
    val items: List<FoodItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
