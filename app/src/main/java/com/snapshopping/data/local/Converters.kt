package com.snapshopping.data.local

import androidx.room.TypeConverter
import com.snapshopping.data.model.FoodCategory

class Converters {
    @TypeConverter
    fun fromFoodCategory(category: FoodCategory): String {
        return category.name
    }

    @TypeConverter
    fun toFoodCategory(value: String): FoodCategory {
        return FoodCategory.fromString(value)
    }
}
