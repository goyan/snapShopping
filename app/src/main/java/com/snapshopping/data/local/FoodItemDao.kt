package com.snapshopping.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.snapshopping.data.model.FoodItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {

    @Query("SELECT * FROM food_items ORDER BY addedAt DESC")
    fun getAllItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getItemById(id: String): FoodItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: FoodItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<FoodItem>)

    @Update
    suspend fun updateItem(item: FoodItem)

    @Delete
    suspend fun deleteItem(item: FoodItem)

    @Query("DELETE FROM food_items WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("DELETE FROM food_items")
    suspend fun deleteAllItems()

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<FoodItem>>
}
