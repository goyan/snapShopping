package com.snapshopping.ui.camera

import android.graphics.Bitmap
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.snapshopping.data.model.FoodCategory
import com.snapshopping.data.model.FoodItem
import com.snapshopping.data.model.ScanState
import com.snapshopping.data.repository.FoodInventoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewModelTest {

    private lateinit var viewModel: CameraViewModel
    private lateinit var mockRepository: FoodInventoryRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
        viewModel = CameraViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() = runTest {
        assertThat(viewModel.scanState.value).isEqualTo(ScanState.Idle)
    }

    @Test
    fun `initial captured photos is empty`() = runTest {
        assertThat(viewModel.capturedPhotos.value).isEmpty()
    }

    @Test
    fun `addPhoto adds bitmap to list`() = runTest {
        // Given
        val mockBitmap = mockk<Bitmap>()

        // When
        viewModel.addPhoto(mockBitmap)

        // Then
        assertThat(viewModel.capturedPhotos.value).hasSize(1)
        assertThat(viewModel.capturedPhotos.value[0]).isEqualTo(mockBitmap)
    }

    @Test
    fun `addPhoto can add multiple photos`() = runTest {
        // Given
        val bitmap1 = mockk<Bitmap>()
        val bitmap2 = mockk<Bitmap>()
        val bitmap3 = mockk<Bitmap>()

        // When
        viewModel.addPhoto(bitmap1)
        viewModel.addPhoto(bitmap2)
        viewModel.addPhoto(bitmap3)

        // Then
        assertThat(viewModel.capturedPhotos.value).hasSize(3)
    }

    @Test
    fun `removePhoto removes photo at index`() = runTest {
        // Given
        val bitmap1 = mockk<Bitmap>()
        val bitmap2 = mockk<Bitmap>()
        viewModel.addPhoto(bitmap1)
        viewModel.addPhoto(bitmap2)

        // When
        viewModel.removePhoto(0)

        // Then
        assertThat(viewModel.capturedPhotos.value).hasSize(1)
        assertThat(viewModel.capturedPhotos.value[0]).isEqualTo(bitmap2)
    }

    @Test
    fun `removePhoto does nothing for invalid index`() = runTest {
        // Given
        val bitmap = mockk<Bitmap>()
        viewModel.addPhoto(bitmap)

        // When
        viewModel.removePhoto(5) // Invalid index

        // Then
        assertThat(viewModel.capturedPhotos.value).hasSize(1)
    }

    @Test
    fun `clearPhotos removes all photos`() = runTest {
        // Given
        viewModel.addPhoto(mockk())
        viewModel.addPhoto(mockk())
        viewModel.addPhoto(mockk())

        // When
        viewModel.clearPhotos()

        // Then
        assertThat(viewModel.capturedPhotos.value).isEmpty()
    }

    @Test
    fun `analyzePhotos does nothing when no photos`() = runTest {
        // When
        viewModel.analyzePhotos()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.scanState.value).isEqualTo(ScanState.Idle)
        coVerify(exactly = 0) { mockRepository.analyzeImages(any()) }
    }

    @Test
    fun `analyzePhotos sets Processing state`() = runTest {
        // Given
        val bitmap = mockk<Bitmap>()
        viewModel.addPhoto(bitmap)
        coEvery { mockRepository.analyzeImages(any()) } returns Result.success(emptyList())

        // When
        viewModel.scanState.test {
            assertThat(awaitItem()).isEqualTo(ScanState.Idle)
            viewModel.analyzePhotos()
            assertThat(awaitItem()).isEqualTo(ScanState.Processing(1))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `analyzePhotos sets Success state on success`() = runTest {
        // Given
        val bitmap = mockk<Bitmap>()
        val items = listOf(
            FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f)
        )
        viewModel.addPhoto(bitmap)
        coEvery { mockRepository.analyzeImages(any()) } returns Result.success(items)

        // When
        viewModel.analyzePhotos()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.scanState.value
        assertThat(state).isInstanceOf(ScanState.Success::class.java)
        assertThat((state as ScanState.Success).items).isEqualTo(items)
    }

    @Test
    fun `analyzePhotos saves items to repository on success`() = runTest {
        // Given
        val bitmap = mockk<Bitmap>()
        val items = listOf(
            FoodItem(name = "Milk", category = FoodCategory.DAIRY, confidence = 0.9f)
        )
        viewModel.addPhoto(bitmap)
        coEvery { mockRepository.analyzeImages(any()) } returns Result.success(items)

        // When
        viewModel.analyzePhotos()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { mockRepository.addItems(items) }
    }

    @Test
    fun `analyzePhotos clears photos on success`() = runTest {
        // Given
        val bitmap = mockk<Bitmap>()
        viewModel.addPhoto(bitmap)
        coEvery { mockRepository.analyzeImages(any()) } returns Result.success(emptyList())

        // When
        viewModel.analyzePhotos()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.capturedPhotos.value).isEmpty()
    }

    @Test
    fun `analyzePhotos sets Error state on failure`() = runTest {
        // Given
        val bitmap = mockk<Bitmap>()
        viewModel.addPhoto(bitmap)
        coEvery { mockRepository.analyzeImages(any()) } returns Result.failure(Exception("API Error"))

        // When
        viewModel.analyzePhotos()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.scanState.value
        assertThat(state).isInstanceOf(ScanState.Error::class.java)
        assertThat((state as ScanState.Error).message).isEqualTo("API Error")
    }

    @Test
    fun `resetState sets state back to Idle`() = runTest {
        // Given
        val bitmap = mockk<Bitmap>()
        viewModel.addPhoto(bitmap)
        coEvery { mockRepository.analyzeImages(any()) } returns Result.failure(Exception("Error"))
        viewModel.analyzePhotos()
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.scanState.value).isInstanceOf(ScanState.Error::class.java)

        // When
        viewModel.resetState()

        // Then
        assertThat(viewModel.scanState.value).isEqualTo(ScanState.Idle)
    }
}
