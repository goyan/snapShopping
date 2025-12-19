package com.snapshopping.ui.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snapshopping.data.model.ScanState
import com.snapshopping.data.repository.FoodInventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: FoodInventoryRepository
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _capturedPhotos = MutableStateFlow<List<Bitmap>>(emptyList())
    val capturedPhotos: StateFlow<List<Bitmap>> = _capturedPhotos.asStateFlow()

    /**
     * Add a captured photo to the list
     */
    fun addPhoto(bitmap: Bitmap) {
        _capturedPhotos.value = _capturedPhotos.value + bitmap
    }

    /**
     * Remove a photo from the list by index
     */
    fun removePhoto(index: Int) {
        _capturedPhotos.value = _capturedPhotos.value.toMutableList().apply {
            if (index in indices) removeAt(index)
        }
    }

    /**
     * Clear all captured photos
     */
    fun clearPhotos() {
        _capturedPhotos.value = emptyList()
    }

    /**
     * Analyze captured photos using Vision API
     */
    fun analyzePhotos() {
        val photos = _capturedPhotos.value
        if (photos.isEmpty()) return

        viewModelScope.launch {
            _scanState.value = ScanState.Processing(photos.size)

            val result = repository.analyzeImages(photos)

            result.fold(
                onSuccess = { items ->
                    // Save items to database
                    repository.addItems(items)
                    _scanState.value = ScanState.Success(items)
                    clearPhotos()
                },
                onFailure = { error ->
                    _scanState.value = ScanState.Error(
                        error.message ?: "Failed to analyze images"
                    )
                }
            )
        }
    }

    /**
     * Reset state to idle (for retry)
     */
    fun resetState() {
        _scanState.value = ScanState.Idle
    }
}
