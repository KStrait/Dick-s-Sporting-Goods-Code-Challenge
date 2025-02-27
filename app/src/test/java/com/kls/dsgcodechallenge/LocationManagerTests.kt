import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.kls.dsgcodechallenge.manager.LocationManager
import com.kls.dsgcodechallenge.util.PermissionChecker
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

class LocationManagerTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockFusedLocationClient: FusedLocationProviderClient

    @Mock
    private lateinit var mockLocation: Location

    @Mock private lateinit var permissionChecker: PermissionChecker

    private lateinit var locationManager: LocationManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        locationManager = LocationManager(mockContext, mockFusedLocationClient, permissionChecker)
    }

    @Test
    fun `getLocation returns location when permission is granted`() = runTest {
        // Mock permission check to return true
        whenever(permissionChecker.hasLocationPermission()).thenReturn(true)

        // Mock CancellationTokenSource and CancellationToken
        val mockCancellationTokenSource = mock<CancellationTokenSource>()
        val mockCancellationToken = mock<CancellationToken>()
        whenever(mockCancellationTokenSource.token).thenReturn(mockCancellationToken)

        // Mock Task<Location>
        val mockLocationTask = mock<Task<Location>>()
        whenever(mockLocationTask.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as OnSuccessListener<Location>
            listener.onSuccess(mockLocation) // Simulate success
            mockLocationTask
        }
        whenever(mockLocationTask.addOnFailureListener(any())).thenReturn(mockLocationTask)

        // Ensure getCurrentLocation() returns the mocked task
        whenever(
            mockFusedLocationClient.getCurrentLocation(
                eq(Priority.PRIORITY_BALANCED_POWER_ACCURACY),
                any()
            )
        ).thenReturn(mockLocationTask)

        // Call getLocation()
        val result = locationManager.getLocation()

        // Verify the result
        assertEquals(mockLocation, result)
    }

    @Test
    fun `getLocation returns null when permission is denied`() = runTest {
        // Mock permission check to return false
        whenever(permissionChecker.hasLocationPermission()).thenReturn(false)

        val result = locationManager.getLocation()

        // Verify that the result is null
        assertNull(result)
    }

    @Test
    fun `getLocation returns null when location is not available`() = runTest {
        // Mock permission check to return true
        whenever(permissionChecker.hasLocationPermission()).thenReturn(true)

        // Mock CancellationTokenSource and CancellationToken
        val mockCancellationTokenSource = mock<CancellationTokenSource>()
        val mockCancellationToken = mock<CancellationToken>()
        whenever(mockCancellationTokenSource.token).thenReturn(mockCancellationToken)

        // Mock Task<Location> that returns null
        val mockLocationTask = mock<Task<Location>>()
        whenever(mockLocationTask.addOnSuccessListener(any())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as OnSuccessListener<Location>
            listener.onSuccess(null) // Simulate no location available
            mockLocationTask
        }
        whenever(mockLocationTask.addOnFailureListener(any())).thenReturn(mockLocationTask)

        // Ensure getCurrentLocation() returns the mocked task
        whenever(
            mockFusedLocationClient.getCurrentLocation(
                eq(Priority.PRIORITY_BALANCED_POWER_ACCURACY),
                any()
            )
        ).thenReturn(mockLocationTask)

        // Call getLocation()
        val result = locationManager.getLocation()

        // Verify the result is null
        assertNull(result)
    }
}