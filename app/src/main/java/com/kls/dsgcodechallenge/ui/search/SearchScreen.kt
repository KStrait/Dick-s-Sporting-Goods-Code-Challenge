package com.kls.dsgcodechallenge.ui.search

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kls.dsgcodechallenge.R
import com.kls.dsgcodechallenge.data.StoreResult
import com.kls.dsgcodechallenge.data.NetworkResult
import com.kls.dsgcodechallenge.extensions.capitalizeWords
import com.kls.dsgcodechallenge.manager.LocationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun StoreListScreen(searchViewModel: SearchViewModel = viewModel()) {
    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }
    var isPermissionGranted by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    )}
    val storeItems by searchViewModel.storeResponse.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            isPermissionGranted = granted
            if (granted) {
                fetchLocationAndSearch(locationManager, searchViewModel)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xfff5f5f5))
            .padding(16.dp)
    ) {
        SearchBar(
            onSearch = { zip ->
                searchViewModel.getStoresByDistance(zip)
            },
            onGpsSearch = {
                if (isPermissionGranted) {
                    fetchLocationAndSearch(locationManager, searchViewModel)
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        when (val data = storeItems) {
            is NetworkResult.Error -> Log.d("Stores", data.exception.toString())
            NetworkResult.Loading -> Log.d("Stores", "Loading")
            is NetworkResult.Success -> {
                val items = data.data
                if (items.isNotEmpty()) {
                    StoreList(stores = items)
                } else {
                    NoStoresView()
                }
            }
        }
    }
}

@Composable
fun SearchBar(onSearch: (String) -> Unit, onGpsSearch: () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xffededed), shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(R.drawable.ic_search), contentDescription = "Search")
        Spacer(modifier = Modifier.width(8.dp))
        TextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
            },
            placeholder = { Text(text = stringResource(id = R.string.search_by_zip)) },
            textStyle = TextStyle(color = Color(0xff7b7b7b), fontSize = 16.sp),
            modifier = Modifier
                .border(BorderStroke(0.dp, Color.Transparent)),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onSearch(searchText)
                }
            ),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Icon(
            painter = painterResource(R.drawable.ic_loc_service),
            contentDescription = "Search by GPS coordinates",
            tint = Color.Unspecified,
            modifier = Modifier.clickable {
                onGpsSearch()
            }
        )
    }
}

@Composable
fun StoreList(stores: List<StoreResult>) {
    Text(
        text = stringResource(id = R.string.stores_near_you),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        LazyColumn {
            itemsIndexed(stores) { index, store ->
                StoreItem(store)
                if (index < stores.lastIndex) {
                    HorizontalDivider(Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

@Composable
fun StoreItem(store: StoreResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = capitalizeWords(store.store.name),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "${store.distance} Miles | ${capitalizeWords(store.store.city)}, ${store.store.state}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Icon(painter = painterResource(R.drawable.ic_chev_right), contentDescription = "Go")
    }
}

@Composable
fun NoStoresView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painterResource(id = R.drawable.ic_empty_search),
            contentDescription = "No stores found"
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.fillMaxWidth(0.65f),
            text = stringResource(id = R.string.no_stores_found)
        )
    }
}

private fun fetchLocationAndSearch(
    locationManager: LocationManager,
    searchViewModel: SearchViewModel
) {
    CoroutineScope(Dispatchers.IO).launch {
        val loc = locationManager.getLocation()
        withContext(Dispatchers.Main) {
            if (loc != null) {
                searchViewModel.getStoresByDistance("${loc.latitude}, ${loc.longitude}")
            } else {
                Log.d("Search", "Location is null.")
            }
        }
    }
}