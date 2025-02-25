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

}