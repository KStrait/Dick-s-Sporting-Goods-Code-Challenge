package com.kls.dsgcodechallenge.ui.search

import android.location.Location
import android.util.Log
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: DSGRepository, locManager: LocationManager, permChecker: PermissionChecker) : ViewModel() {

    var locationManager = locManager
    var permissionChecker = permChecker

    private val _storeResponse = MutableStateFlow<List<StoreResult>?>(null)
    val storeResponse: StateFlow<List<StoreResult>?> = _storeResponse

    fun getStoresByDistance(addr: String) {
        viewModelScope.launch {
            repository.getStoresByDistance(addr).collect { items ->
                _storeResponse.value = items
            }
        }
    }
    fun fetchLocationAndSearch() {
        CoroutineScope(Dispatchers.IO).launch {
            val loc = locationManager.getLocation()
            withContext(Dispatchers.Main) {
                if (loc != null) {
                    getStoresByDistance("${loc.latitude}, ${loc.longitude}")
                } else {
                    // No location, requirements do not mention expected handling of situation.
                }
            }
        }
    }
}