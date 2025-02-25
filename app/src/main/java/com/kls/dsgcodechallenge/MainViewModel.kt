package com.kls.dsgcodechallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kls.dsgcodechallenge.repo.DSGRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.kls.dsgcodechallenge.data.NetworkResult
import com.kls.dsgcodechallenge.data.StoreResult

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: DSGRepository): ViewModel() {

    private val _storeResponse = MutableStateFlow<NetworkResult<List<StoreResult>>>(NetworkResult.Loading)
    val storeResponse: StateFlow<NetworkResult<List<StoreResult>>> = _storeResponse

    init {
        getStoresByDistance("48220")
    }

    fun getStoresByDistance(addr: String) {
        viewModelScope.launch {
            repository.getStoresByDistance(addr).collect { items ->
                _storeResponse.value = items
            }
        }
    }
}