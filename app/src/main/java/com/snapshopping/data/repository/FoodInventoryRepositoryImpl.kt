package com.snapshopping.data.repository

import android.graphics.Bitmap
import com.snapshopping.data.local.FoodItemDao
import com.snapshopping.data.model.DetectedFood
import com.snapshopping.data.model.FoodCategory
import com.snapshopping.data.model.FoodItem
import com.snapshopping.data.remote.VisionApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodInventoryRepositoryImpl @Inject constructor(
    private val foodItemDao: FoodItemDao,
    private val visionApiService: VisionApiService
) : FoodInventoryRepository {

    companion object {
        private const val CONFIDENCE_THRESHOLD = 0.6f
    }

    override fun getAllItems(): Flow<List<FoodItem>> {
        return foodItemDao.getAllItems()
    }

    override suspend fun getItemById(id: String): FoodItem? {
        return foodItemDao.getItemById(id)
    }

    override suspend fun addItem(item: FoodItem) {
        foodItemDao.insertItem(item)
    }

    override suspend fun addItems(items: List<FoodItem>) {
        foodItemDao.insertItems(items)
    }

    override suspend fun updateItem(item: FoodItem) {
        foodItemDao.updateItem(item)
    }

    override suspend fun deleteItem(item: FoodItem) {
        foodItemDao.deleteItem(item)
    }

    override suspend fun deleteItemById(id: String) {
        foodItemDao.deleteItemById(id)
    }

    override suspend fun clearInventory() {
        foodItemDao.deleteAllItems()
    }

    override fun searchItems(query: String): Flow<List<FoodItem>> {
        return foodItemDao.searchItems(query)
    }

    override suspend fun analyzeImages(images: List<Bitmap>): Result<List<FoodItem>> {
        val result = visionApiService.analyzeImages(images)

        return result.map { detectedFoods ->
            processDetectedFoods(detectedFoods)
        }
    }

    /**
     * Process detected foods:
     * - Filter low confidence items
     * - Normalize names (singular form)
     * - Merge duplicates
     * - Convert to FoodItem entities
     */
    private fun processDetectedFoods(detectedFoods: List<DetectedFood>): List<FoodItem> {
        return detectedFoods
            // Filter low confidence items
            .filter { it.confidence >= CONFIDENCE_THRESHOLD }
            // Normalize names and group duplicates
            .groupBy { normalizeName(it.name) }
            .map { (normalizedName, items) ->
                // Take the highest confidence for duplicates
                val bestItem = items.maxBy { it.confidence }
                FoodItem(
                    name = normalizedName,
                    category = FoodCategory.fromString(bestItem.category),
                    confidence = bestItem.confidence,
                    quantity = items.size // Count as quantity if multiple detected
                )
            }
            .sortedByDescending { it.confidence }
    }

    /**
     * Normalize food name:
     * - Convert to lowercase
     * - Remove common plurals
     * - Trim whitespace
     */
    private fun normalizeName(name: String): String {
        var normalized = name.lowercase().trim()

        // Simple plural to singular conversion
        val pluralRules = listOf(
            "ies" to "y",      // berries -> berry
            "ves" to "f",       // leaves -> leaf
            "oes" to "o",       // tomatoes -> tomato
            "es" to "",         // boxes -> box (but careful with cheese)
            "s" to ""           // apples -> apple
        )

        // Skip words that shouldn't be singularized
        val exceptions = setOf(
            "cheese", "lettuce", "rice", "juice", "sauce",
            "hummus", "asparagus", "broccoli", "celery"
        )

        if (normalized !in exceptions) {
            for ((plural, singular) in pluralRules) {
                if (normalized.endsWith(plural) && normalized.length > plural.length + 2) {
                    normalized = normalized.dropLast(plural.length) + singular
                    break
                }
            }
        }

        // Capitalize first letter
        return normalized.replaceFirstChar { it.uppercase() }
    }
}
