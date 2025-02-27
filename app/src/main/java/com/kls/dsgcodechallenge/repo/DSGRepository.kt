package com.kls.dsgcodechallenge.repo

import com.kls.dsgcodechallenge.data.StoreResult
import com.kls.dsgcodechallenge.network.WebService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton
import com.kls.dsgcodechallenge.data.NetworkResult

@Singleton
class DSGRepository @Inject constructor(private val webService: WebService) {


    // Make API endpoint call to get stores, return exception if any errors.
    fun getStoresByDistance(addr: String): Flow<NetworkResult<List<StoreResult>>> = flow {
        try {
            emit(NetworkResult.Loading)
            val data = webService.getStoresByDistance(addr).results
            emit(NetworkResult.Success(data))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}