package com.kls.dsgcodechallenge.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kls.dsgcodechallenge.data.NetworkResult
import com.kls.dsgcodechallenge.data.StoreResult
import com.kls.dsgcodechallenge.repo.DSGRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: DSGRepository) : ViewModel() {

    private val _storeResponse = MutableStateFlow<NetworkResult<List<StoreResult>>>(NetworkResult.Loading)
    val storeResponse: StateFlow<NetworkResult<List<StoreResult>>> = _storeResponse

    fun getStoresByDistance(addr: String) {
        viewModelScope.launch {
            repository.getStoresByDistance(addr).collect { items ->
                _storeResponse.value = items
            }
        }
    }
}