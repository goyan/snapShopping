package com.snapshopping.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.snapshopping.data.model.FoodItem

@Database(
    entities = [FoodItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SnapShoppingDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
}
