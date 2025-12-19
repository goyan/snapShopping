package com.snapshopping.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.snapshopping.data.model.FoodCategory
import com.snapshopping.data.model.FoodItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoodItemDaoTest {

    private lateinit var database: SnapShoppingDatabase
    private lateinit var dao: FoodItemDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            SnapShoppingDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.foodItemDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveItem() = runTest {
        // Given
        val item = FoodItem(
            id = "test-id-1",
            name = "Milk",
            category = FoodCategory.DAIRY,
            confidence = 0.95f,
            quantity = 1
        )

        // When
        dao.insertItem(item)
        val items = dao.getAllItems().first()

        // Then
        assertThat(items).hasSize(1)
        assertThat(items[0].name).isEqualTo("Milk")
        assertThat(items[0].category).isEqualTo(FoodCategory.DAIRY)
        assertThat(items[0].confidence).isEqualTo(0.95f)
    }

    @Test
    fun insertMultipleItems() = runTest {
        // Given
        val items = listOf(
            FoodItem(id = "1", name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f),
            FoodItem(id = "2", name = "Apple", category = FoodCategory.FRUITS, confidence = 0.85f),
            FoodItem(id = "3", name = "Cheese", category = FoodCategory.DAIRY, confidence = 0.92f)
        )

        // When
        dao.insertItems(items)
        val result = dao.getAllItems().first()

        // Then
        assertThat(result).hasSize(3)
    }

    @Test
    fun getItemById() = runTest {
        // Given
        val item = FoodItem(
            id = "unique-id",
            name = "Apple",
            category = FoodCategory.FRUITS,
            confidence = 0.88f
        )
        dao.insertItem(item)

        // When
        val result = dao.getItemById("unique-id")

        // Then
        assertThat(result).isNotNull()
        assertThat(result?.name).isEqualTo("Apple")
    }

    @Test
    fun getItemByIdReturnsNullForNonExistent() = runTest {
        // When
        val result = dao.getItemById("non-existent-id")

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun updateItem() = runTest {
        // Given
        val item = FoodItem(
            id = "update-test",
            name = "Milk",
            category = FoodCategory.DAIRY,
            confidence = 0.9f,
            quantity = 1
        )
        dao.insertItem(item)

        // When
        val updatedItem = item.copy(name = "Skim Milk", quantity = 2)
        dao.updateItem(updatedItem)
        val result = dao.getItemById("update-test")

        // Then
        assertThat(result?.name).isEqualTo("Skim Milk")
        assertThat(result?.quantity).isEqualTo(2)
    }

    @Test
    fun deleteItem() = runTest {
        // Given
        val item = FoodItem(
            id = "delete-test",
            name = "Cheese",
            category = FoodCategory.DAIRY,
            confidence = 0.9f
        )
        dao.insertItem(item)
        assertThat(dao.getAllItems().first()).hasSize(1)

        // When
        dao.deleteItem(item)

        // Then
        assertThat(dao.getAllItems().first()).isEmpty()
    }

    @Test
    fun deleteItemById() = runTest {
        // Given
        val item = FoodItem(
            id = "delete-by-id-test",
            name = "Butter",
            category = FoodCategory.DAIRY,
            confidence = 0.87f
        )
        dao.insertItem(item)

        // When
        dao.deleteItemById("delete-by-id-test")

        // Then
        assertThat(dao.getItemById("delete-by-id-test")).isNull()
    }

    @Test
    fun deleteAllItems() = runTest {
        // Given
        dao.insertItems(listOf(
            FoodItem(id = "1", name = "A", category = FoodCategory.OTHER, confidence = 0.9f),
            FoodItem(id = "2", name = "B", category = FoodCategory.OTHER, confidence = 0.9f),
            FoodItem(id = "3", name = "C", category = FoodCategory.OTHER, confidence = 0.9f)
        ))
        assertThat(dao.getAllItems().first()).hasSize(3)

        // When
        dao.deleteAllItems()

        // Then
        assertThat(dao.getAllItems().first()).isEmpty()
    }

    @Test
    fun searchItemsByName() = runTest {
        // Given
        dao.insertItems(listOf(
            FoodItem(id = "1", name = "Apple", category = FoodCategory.FRUITS, confidence = 0.9f),
            FoodItem(id = "2", name = "Apple Juice", category = FoodCategory.BEVERAGES, confidence = 0.85f),
            FoodItem(id = "3", name = "Banana", category = FoodCategory.FRUITS, confidence = 0.88f)
        ))

        // When
        val results = dao.searchItems("Apple").first()

        // Then
        assertThat(results).hasSize(2)
        assertThat(results.map { it.name }).containsExactly("Apple", "Apple Juice")
    }

    @Test
    fun searchItemsReturnsEmptyForNoMatch() = runTest {
        // Given
        dao.insertItem(
            FoodItem(id = "1", name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f)
        )

        // When
        val results = dao.searchItems("Orange").first()

        // Then
        assertThat(results).isEmpty()
    }

    @Test
    fun itemsOrderedByAddedAtDescending() = runTest {
        // Given - Insert with different timestamps
        val item1 = FoodItem(id = "1", name = "First", category = FoodCategory.OTHER, confidence = 0.9f, addedAt = 1000L)
        val item2 = FoodItem(id = "2", name = "Second", category = FoodCategory.OTHER, confidence = 0.9f, addedAt = 2000L)
        val item3 = FoodItem(id = "3", name = "Third", category = FoodCategory.OTHER, confidence = 0.9f, addedAt = 3000L)
        dao.insertItems(listOf(item1, item2, item3))

        // When
        val results = dao.getAllItems().first()

        // Then - Most recent first
        assertThat(results[0].name).isEqualTo("Third")
        assertThat(results[1].name).isEqualTo("Second")
        assertThat(results[2].name).isEqualTo("First")
    }

    @Test
    fun insertWithConflictReplaces() = runTest {
        // Given
        val original = FoodItem(
            id = "same-id",
            name = "Original",
            category = FoodCategory.DAIRY,
            confidence = 0.9f
        )
        dao.insertItem(original)

        // When - Insert with same ID
        val replacement = FoodItem(
            id = "same-id",
            name = "Replaced",
            category = FoodCategory.MEAT,
            confidence = 0.95f
        )
        dao.insertItem(replacement)

        // Then
        val items = dao.getAllItems().first()
        assertThat(items).hasSize(1)
        assertThat(items[0].name).isEqualTo("Replaced")
        assertThat(items[0].category).isEqualTo(FoodCategory.MEAT)
    }

    @Test
    fun categoryConverterWorksCorrectly() = runTest {
        // Given - Insert items with all categories
        val categories = FoodCategory.entries
        categories.forEachIndexed { index, category ->
            dao.insertItem(
                FoodItem(
                    id = "cat-$index",
                    name = "Item $index",
                    category = category,
                    confidence = 0.9f
                )
            )
        }

        // When
        val items = dao.getAllItems().first()

        // Then
        assertThat(items).hasSize(categories.size)
        items.forEachIndexed { index, item ->
            assertThat(item.category).isIn(categories)
        }
    }
}
