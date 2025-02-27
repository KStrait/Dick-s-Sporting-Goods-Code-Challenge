package com.kls.dsgcodechallenge

import android.location.Location
import com.kls.dsgcodechallenge.data.NetworkResult
import com.kls.dsgcodechallenge.data.StoreResult
import com.kls.dsgcodechallenge.data.Store
import com.kls.dsgcodechallenge.data.StoreHours
import com.kls.dsgcodechallenge.manager.LocationManager
import com.kls.dsgcodechallenge.repo.DSGRepository
import com.kls.dsgcodechallenge.ui.search.SearchViewModel
import com.kls.dsgcodechallenge.util.PermissionChecker
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockRepository: DSGRepository

    @Mock
    private lateinit var mockLocationManager: LocationManager

    @Mock
    private lateinit var mockPermissionChecker: PermissionChecker

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(
            repository = mockRepository,
            locManager = mockLocationManager,
            permChecker = mockPermissionChecker
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getStoresByDistance should update state to success with stores from repository`() = runTest {
        // Given
        val zipCode = "12345"

        val storeHours = StoreHours(
            sun = "10:00 AM - 6:00 PM",
            mon = "9:00 AM - 9:00 PM",
            tue = "9:00 AM - 9:00 PM",
            wed = "9:00 AM - 9:00 PM",
            thu = "9:00 AM - 9:00 PM",
            fri = "9:00 AM - 9:00 PM",
            sat = "9:00 AM - 9:00 PM"
        )

        val store1 = Store(
            location = "123",
            chain = "DSG",
            name = "Downtown Store",
            street1 = "123 Main St",
            street2 = null,
            phone = "555-1234",
            city = "Pittsburgh",
            state = "PA",
            zip = "15222",
            country = "US",
            lat = "40.4406",
            lng = "-79.9959",
            storeHours = storeHours,
            curbsideHours = storeHours,
            status = "OPEN"
        )

        val store2 = Store(
            location = "456",
            chain = "DSG",
            name = "Suburb Store",
            street1 = "456 Oak Ave",
            street2 = "Suite 100",
            phone = "555-5678",
            city = "Pittsburgh",
            state = "PA",
            zip = "15237",
            country = "US",
            lat = "40.5471",
            lng = "-80.0149",
            storeHours = storeHours,
            curbsideHours = null,
            status = "OPEN"
        )

        val storeResults = listOf(
            StoreResult(
                store = store1,
                distance = "3.5",
                units = "mi"
            ),
            StoreResult(
                store = store2,
                distance = "7.2",
                units = "mi"
            )
        )

        val repositoryFlow = flow {
            // First emit loading state
            emit(NetworkResult.Loading)
            // Then emit success state with data
            emit(NetworkResult.Success(storeResults))
        }

        // When
        `when`(mockRepository.getStoresByDistance(zipCode)).thenReturn(repositoryFlow)
        viewModel.getStoresByDistance(zipCode)

        // Then - advance the test dispatcher to collect initial loading state
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify repository was called with the zip code
        verify(mockRepository).getStoresByDistance(zipCode)

        // Verify the storeResponse state was updated with the success result
        val result = viewModel.storeResponse.value
        assertTrue(result is NetworkResult.Success)
        assertEquals(storeResults, (result as NetworkResult.Success).data)
    }

    @Test
    fun `getStoresByDistance should handle empty results from repository`() = runTest {
        // Given
        val zipCode = "12345"
        val emptyList = emptyList<StoreResult>()

        val repositoryFlow = flow {
            emit(NetworkResult.Loading)
            emit(NetworkResult.Success(emptyList))
        }

        // When
        `when`(mockRepository.getStoresByDistance(zipCode)).thenReturn(repositoryFlow)
        viewModel.getStoresByDistance(zipCode)

        // Then - advance the test dispatcher
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify repository was called
        verify(mockRepository).getStoresByDistance(zipCode)

        // Verify the storeResponse state was updated with a success result containing empty list
        val result = viewModel.storeResponse.value
        assertTrue(result is NetworkResult.Success)
        assertTrue((result as NetworkResult.Success).data.isEmpty())
    }

    @Test
    fun `getStoresByDistance should handle error from repository`() = runTest {
        // Given
        val zipCode = "12345"
        val errorMessage = "Network error"

        val repositoryFlow = flow {
            emit(NetworkResult.Loading)
            emit(NetworkResult.Error(Exception(errorMessage)))
        }

        // When
        `when`(mockRepository.getStoresByDistance(zipCode)).thenReturn(repositoryFlow)
        viewModel.getStoresByDistance(zipCode)

        // Then - advance the test dispatcher
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify repository was called
        verify(mockRepository).getStoresByDistance(zipCode)

        // Verify the storeResponse state was updated with an error result
        val result = viewModel.storeResponse.value
        assertTrue(result is NetworkResult.Error)
        assertEquals(errorMessage, (result as NetworkResult.Error).exception.message)
    }

    @Test
    fun `getStoresByDistance should start with loading state`() = runTest {
        // Given
        val zipCode = "12345"

        // Repository will never emit to simulate long-running operation
        val repositoryFlow = flow<NetworkResult<List<StoreResult>>> {
            emit(NetworkResult.Loading)
            // No further emissions
        }

        // When
        `when`(mockRepository.getStoresByDistance(zipCode)).thenReturn(repositoryFlow)
        viewModel.getStoresByDistance(zipCode)

        // Then - advance the test dispatcher to collect only loading state
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify the storeResponse state was updated with loading state
        val result = viewModel.storeResponse.value
        assertTrue(result is NetworkResult.Loading)
    }

    @Test
    fun `storeResponse should be loading initially`() = runTest {
        // Then - verify the initial state is idle
        val result = viewModel.storeResponse.value
        assertTrue(result is NetworkResult.Loading)
    }
}