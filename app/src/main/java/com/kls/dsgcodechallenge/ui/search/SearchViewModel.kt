package com.kls.dsgcodechallenge.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kls.dsgcodechallenge.data.NetworkResult
import com.kls.dsgcodechallenge.data.StoreResult
import com.kls.dsgcodechallenge.manager.LocationManager
import com.kls.dsgcodechallenge.repo.DSGRepository
import com.kls.dsgcodechallenge.util.PermissionChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: DSGRepository, locManager: LocationManager, permChecker: PermissionChecker) : ViewModel() {

    private var locationManager = locManager
    var permissionChecker = permChecker

    private val _storeResponse = MutableStateFlow<NetworkResult<List<StoreResult>>>(NetworkResult.Loading)
    val storeResponse: StateFlow<NetworkResult<List<StoreResult>>> = _storeResponse

    private val locationLoading = MutableStateFlow(false)
    private val storesLoading = MutableStateFlow(false)

    // Combined loading state, rather than observing Stores/Location loading separately.
    val loading: StateFlow<Boolean> = combine(
        locationLoading,
        storesLoading
    ) { locLoading, storesLoading ->
        locLoading || storesLoading
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false)

    private val _locationError = MutableStateFlow<Boolean>(false)
    val locationError = _locationError.asStateFlow()

    // Call repo to get stores
    fun getStoresByDistance(addr: String) {
        viewModelScope.launch {
            storesLoading.value = true
            repository.getStoresByDistance(addr).collect { items ->
                _storeResponse.value = items
                storesLoading.value = false
            }
        }
    }

    // Get location, if location not null get stores, else show error dialog
    fun fetchLocationAndSearch() {
        CoroutineScope(Dispatchers.IO).launch {
            locationLoading.value = true
            val loc = locationManager.getLocation()
            withContext(Dispatchers.Main) {
                if (loc != null) {
                    getStoresByDistance("${loc.latitude}, ${loc.longitude}")
                } else {
                    _locationError.value = true
                }
                locationLoading.value = false
            }
        }
    }

    // Clear error to prevent dialog from showing repeatedly
    fun clearLocationError() {
        _locationError.value = false
    }
}