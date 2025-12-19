package com.snapshopping.di

import android.content.Context
import androidx.room.Room
import com.google.ai.client.generativeai.GenerativeModel
import com.snapshopping.BuildConfig
import com.snapshopping.data.local.FoodItemDao
import com.snapshopping.data.local.SnapShoppingDatabase
import com.snapshopping.data.remote.VisionApiService
import com.snapshopping.data.repository.FoodInventoryRepository
import com.snapshopping.data.repository.FoodInventoryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SnapShoppingDatabase {
        return Room.databaseBuilder(
            context,
            SnapShoppingDatabase::class.java,
            "snapshopping_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFoodItemDao(database: SnapShoppingDatabase): FoodItemDao {
        return database.foodItemDao()
    }

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideVisionApiService(generativeModel: GenerativeModel): VisionApiService {
        return VisionApiService(generativeModel)
    }

    @Provides
    @Singleton
    fun provideFoodInventoryRepository(
        foodItemDao: FoodItemDao,
        visionApiService: VisionApiService
    ): FoodInventoryRepository {
        return FoodInventoryRepositoryImpl(foodItemDao, visionApiService)
    }
}
