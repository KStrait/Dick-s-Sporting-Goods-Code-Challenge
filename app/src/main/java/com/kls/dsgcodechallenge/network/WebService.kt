package com.kls.dsgcodechallenge.network

import com.kls.dsgcodechallenge.data.StoreResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WebService {
    companion object {
        const val BASE_URL = "https://availability.dickssportinggoods.com/api/v4/stores/"
    }

    @GET("search?lob=dsg&radius=100")
    suspend fun getStoresByDistance(@Query("addr") location: String): StoreResponse
}