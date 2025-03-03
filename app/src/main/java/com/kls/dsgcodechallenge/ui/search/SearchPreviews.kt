package com.kls.dsgcodechallenge.ui.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kls.dsgcodechallenge.data.NetworkResult
import com.kls.dsgcodechallenge.data.Store
import com.kls.dsgcodechallenge.data.StoreHours
import com.kls.dsgcodechallenge.data.StoreResult
import com.kls.dsgcodechallenge.ui.theme.DSGCodeChallengeTheme

/**
 * Instead of previewing the entire StoreListScreen with a mocked ViewModel,
 * it's better to preview the individual components separately.
 *
 * This decouples the previews from the ViewModel implementation
 * and makes them more resilient to changes.
 */

@Preview(name = "SearchBar")
@Composable
fun SearchBarPreview() {
    DSGCodeChallengeTheme {
        SearchBar(
            onSearch = {},
            onGpsSearch = {}
        )
    }
}

@Preview(name = "StoreList")
@Composable
fun StoreListPreview() {
    DSGCodeChallengeTheme {
        StoreList(stores = sampleStores)
    }
}

@Preview(name = "StoreItem")
@Composable
fun StoreItemPreview() {
    DSGCodeChallengeTheme {
        StoreItem(store = sampleStores.first())
    }
}

@Preview(name = "NoStoresView")
@Composable
fun NoStoresViewPreview() {
    DSGCodeChallengeTheme {
        NoStoresView()
    }
}

@Preview(name = "LocationErrorDialog")
@Composable
fun LocationErrorDialogPreview() {
    DSGCodeChallengeTheme {
        LocationErrorDialog(clearLocationError = {})
    }
}

@Preview(name = "LoadingView")
@Composable
fun LoadingViewPreview() {
    DSGCodeChallengeTheme {
        LoadingView()
    }
}

// Sample store hours
private val sampleHours = StoreHours(
    sun = "10:00 AM - 6:00 PM",
    mon = "9:00 AM - 9:00 PM",
    tue = "9:00 AM - 9:00 PM",
    wed = "9:00 AM - 9:00 PM",
    thu = "9:00 AM - 9:00 PM",
    fri = "9:00 AM - 10:00 PM",
    sat = "9:00 AM - 10:00 PM"
)

// Mock data for previews
val sampleStores = listOf(
    StoreResult(
        store = Store(
            location = "12345",
            chain = "DSG",
            name = "downtown store",
            street1 = "123 Main St",
            street2 = null,
            phone = "555-123-4567",
            city = "new york",
            state = "NY",
            zip = "10001",
            country = "US",
            lat = "40.7128",
            lng = "-74.0060",
            storeHours = sampleHours,
            curbsideHours = sampleHours,
            status = "OPEN"
        ),
        distance = "0.8",
        units = "Miles"
    ),
    StoreResult(
        store = Store(
            location = "67890",
            chain = "DSG",
            name = "uptown market",
            street1 = "456 Park Ave",
            street2 = "Suite 200",
            phone = "555-987-6543",
            city = "brooklyn",
            state = "NY",
            zip = "11201",
            country = "US",
            lat = "40.6782",
            lng = "-73.9442",
            storeHours = sampleHours,
            curbsideHours = null,
            status = "OPEN"
        ),
        distance = "2.5",
        units = "Miles"
    ),
    StoreResult(
        store = Store(
            location = "24680",
            chain = "DSG",
            name = "west side grocery",
            street1 = "789 Broadway",
            street2 = null,
            phone = "555-246-8101",
            city = "queens",
            state = "NY",
            zip = "11101",
            country = "US",
            lat = "40.7549",
            lng = "-73.9840",
            storeHours = sampleHours,
            curbsideHours = sampleHours,
            status = "OPEN"
        ),
        distance = "4.2",
        units = "Miles"
    )
)

/**
 * For previewing different states of the screen, you can create wrapper composables
 * that simulate the various states without involving a real ViewModel.
 */

@Preview(name = "StoreList Screen - Success State", showBackground = true)
@Composable
fun StoreListScreenSuccessPreview() {
    DSGCodeChallengeTheme {
        // This is a wrapper that shows what the screen would look like in a success state
        // You would need to refactor StoreListScreen to accept these parameters instead of a ViewModel
        // or create a simplified version just for preview
        StoreListScreenContent(
            storeItems = NetworkResult.Success(sampleStores),
            locationError = false,
            loading = false,
            onSearch = {},
            onGpsSearch = {},
            clearLocationError = {}
        )
    }
}

@Preview(name = "StoreList Screen - Loading State", showBackground = true)
@Composable
fun StoreListScreenLoadingPreview() {
    DSGCodeChallengeTheme {
        StoreListScreenContent(
            storeItems = NetworkResult.Loading,
            locationError = false,
            loading = true,
            onSearch = {},
            onGpsSearch = {},
            clearLocationError = {}
        )
    }
}

@Preview(name = "StoreList Screen - Error State", showBackground = true)
@Composable
fun StoreListScreenErrorPreview() {
    DSGCodeChallengeTheme {
        StoreListScreenContent(
            storeItems = NetworkResult.Error(Exception("Network error occurred")),
            locationError = false,
            loading = false,
            onSearch = {},
            onGpsSearch = {},
            clearLocationError = {}
        )
    }
}

@Preview(name = "StoreList Screen - Location Error State", showBackground = true)
@Composable
fun StoreListScreenLocationErrorPreview() {
    DSGCodeChallengeTheme {
        StoreListScreenContent(
            storeItems = NetworkResult.Success(emptyList()),
            locationError = true,
            loading = false,
            onSearch = {},
            onGpsSearch = {},
            clearLocationError = {}
        )
    }
}

// Separate composable for previews, removing ViewModel from constructor
@Composable
fun StoreListScreenContent(
    storeItems: NetworkResult<List<StoreResult>>,
    locationError: Boolean,
    loading: Boolean,
    onSearch: (String) -> Unit,
    onGpsSearch: () -> Unit,
    clearLocationError: () -> Unit
) {
    // This would need to be implemented based on your actual StoreListScreen
    // but without the ViewModel dependency

    // Example implementation (you'll need to adapt to match your actual UI):
    androidx.compose.material3.Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
        if (locationError) {
            LocationErrorDialog(clearLocationError = clearLocationError)
        }

        androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color(0xfff5f5f5))
                    .padding(16.dp)
            ) {
                SearchBar(onSearch = onSearch, onGpsSearch = onGpsSearch)
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

                when (storeItems) {
                    is NetworkResult.Loading -> { /* Empty by design */ }
                    is NetworkResult.Success -> {
                        val items = storeItems.data
                        if (items.isNotEmpty()) {
                            StoreList(stores = items)
                        } else {
                            NoStoresView()
                        }
                    }
                    is NetworkResult.Error -> {
                        storeItems.exception.message?.let {
                            // You would call your NetworkExceptionView here
                            androidx.compose.material3.Text("Error: $it")
                        }
                    }
                }
            }

            if (loading) {
                LoadingView()
            }
        }
    }
}