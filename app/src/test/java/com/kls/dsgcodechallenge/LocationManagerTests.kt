import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.kls.dsgcodechallenge.manager.LocationManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.mockito.Mockito

@RunWith(RobolectricTestRunner::class)
class LocationManagerTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockFusedLocationClient: FusedLocationProviderClient

    @Mock
    private lateinit var mockLocationTask: Task<Location>

    @Mock
    private lateinit var mockLocation: Location

    private lateinit var locationManager: LocationManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        locationManager = LocationManager(mockContext, mockFusedLocationClient)
    }

    @Test
    fun `getLocation returns location when permission is granted`() {
        // Mock the static method for ActivityCompat
        Mockito.mockStatic(ActivityCompat::class.java).use { mockedActivityCompat ->
            // Mock permission check to return PERMISSION_GRANTED
            mockedActivityCompat.`when`<Int> {
                ActivityCompat.checkSelfPermission(mockContext, Manifest.permission.ACCESS_COARSE_LOCATION)
            }.thenReturn(PackageManager.PERMISSION_GRANTED)

            // Mock the Tasks.await method
            Mockito.mockStatic(Tasks::class.java).use { mockedTasks ->
                // Mock FusedLocationClient.lastLocation to return our mock task
                whenever(mockFusedLocationClient.lastLocation).thenReturn(mockLocationTask)

                // Mock Tasks.await to return our mock location
                mockedTasks.`when`<Location> { Tasks.await(mockLocationTask) }.thenReturn(mockLocation)

                // Test the method
                val result = locationManager.getLocation()

                // Verify the result
                assertEquals(mockLocation, result)
            }
        }
    }

    @Test
    fun `getLocation returns null when permission is denied`() {
        // Mock the static method for ActivityCompat
        Mockito.mockStatic(ActivityCompat::class.java).use { mockedActivityCompat ->
            // Mock permission check to return PERMISSION_DENIED
            mockedActivityCompat.`when`<Int> {
                ActivityCompat.checkSelfPermission(mockContext, Manifest.permission.ACCESS_COARSE_LOCATION)
            }.thenReturn(PackageManager.PERMISSION_DENIED)

            // Test the method
            val result = locationManager.getLocation()

            // Verify the result
            assertNull(result)
        }
    }

    @Test
    fun `getLocation returns null when location is not available`() {
        // Mock the static method for ActivityCompat
        Mockito.mockStatic(ActivityCompat::class.java).use { mockedActivityCompat ->
            // Mock permission check to return PERMISSION_GRANTED
            mockedActivityCompat.`when`<Int> {
                ActivityCompat.checkSelfPermission(mockContext, Manifest.permission.ACCESS_COARSE_LOCATION)
            }.thenReturn(PackageManager.PERMISSION_GRANTED)

            // Mock the Tasks.await method
            Mockito.mockStatic(Tasks::class.java).use { mockedTasks ->
                // Mock FusedLocationClient.lastLocation to return our mock task
                whenever(mockFusedLocationClient.lastLocation).thenReturn(mockLocationTask)

                // Mock Tasks.await to return null (no location available)
                mockedTasks.`when`<Location> { Tasks.await(mockLocationTask) }.thenReturn(null)

                // Test the method
                val result = locationManager.getLocation()

                // Verify the result
                assertNull(result)
            }
        }
    }
}