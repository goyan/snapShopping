package com.snapshopping.data.repository

import android.graphics.Bitmap
import com.snapshopping.data.model.FoodItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for food inventory operations
 */
interface FoodInventoryRepository {
    /**
     * Get all food items as a Flow
     */
    fun getAllItems(): Flow<List<FoodItem>>

    /**
     * Get a single item by ID
     */
    suspend fun getItemById(id: String): FoodItem?

    /**
     * Add a new food item
     */
    suspend fun addItem(item: FoodItem)

    /**
     * Add multiple food items
     */
    suspend fun addItems(items: List<FoodItem>)

    /**
     * Update an existing food item
     */
    suspend fun updateItem(item: FoodItem)

    /**
     * Delete a food item
     */
    suspend fun deleteItem(item: FoodItem)

    /**
     * Delete a food item by ID
     */
    suspend fun deleteItemById(id: String)

    /**
     * Clear all items from inventory
     */
    suspend fun clearInventory()

    /**
     * Search items by name
     */
    fun searchItems(query: String): Flow<List<FoodItem>>

    /**
     * Analyze images and detect food items
     * @param images List of compressed bitmap images
     * @return Result containing list of detected food items or error
     */
    suspend fun analyzeImages(images: List<Bitmap>): Result<List<FoodItem>>
}
