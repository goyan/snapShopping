package com.snapshopping.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapshopping.data.model.FoodCategory
import com.snapshopping.data.model.FoodItem
import com.snapshopping.data.model.InventoryUiState
import com.snapshopping.data.repository.FoodInventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: FoodInventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState(isLoading = true))
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    /**
     * Load all items from database
     */
    private fun loadItems() {
        viewModelScope.launch {
            repository.getAllItems()
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load items"
                        )
                    }
                }
                .collect { items ->
                    _uiState.update {
                        it.copy(
                            items = items,
                            isLoading = false
                        )
                    }
                }
        }
    }

    /**
     * Add a new item manually
     */
    fun addItem(name: String, category: FoodCategory, quantity: Int) {
        viewModelScope.launch {
            try {
                val item = FoodItem(
                    name = name,
                    category = category,
                    confidence = 1.0f, // Manual input has 100% confidence
                    quantity = quantity
                )
                repository.addItem(item)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to add item: ${e.message}")
                }
            }
        }
    }

    /**
     * Update an existing item
     */
    fun updateItem(item: FoodItem) {
        viewModelScope.launch {
            try {
                repository.updateItem(item)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to update item: ${e.message}")
                }
            }
        }
    }

    /**
     * Update item quantity
     */
    fun updateQuantity(item: FoodItem, delta: Int) {
        val newQuantity = (item.quantity + delta).coerceAtLeast(1)
        updateItem(item.copy(quantity = newQuantity))
    }

    /**
     * Delete an item
     */
    fun deleteItem(item: FoodItem) {
        viewModelScope.launch {
            try {
                repository.deleteItem(item)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to delete item: ${e.message}")
                }
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Clear all items
     */
    fun clearInventory() {
        viewModelScope.launch {
            try {
                repository.clearInventory()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "Failed to clear inventory: ${e.message}")
                }
            }
        }
    }
}
