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

    fun getStoresByDistance(addr: String): Flow<List<StoreResult>> = flow {
        try {
            val data = webService.getStoresByDistance(addr).results
            emit(data)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
}