package com.kls.dsgcodechallenge.data

sealed class NetworkResult<out T> {
    object Loading : NetworkResult<Nothing>()
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Throwable) : NetworkResult<Nothing>()
}

data class StoreResponse(
    val origin: Origin?,
    val results: List<StoreResult> // Renamed to StoreResult for clarity
)

data class Origin(
    val geocoded_address: String,
    val lat: String,
    val lng: String
)

data class StoreResult( // Represents each entry in the results list
    val store: Store,
    val distance: String,
    val units: String
)

data class Store(
    val location: String,
    val chain: String,
    val name: String,
    val street1: String?,
    val street2: String?,
    val phone: String?,
    val city: String,
    val state: String?,
    val zip: String?,
    val country: String?,
    val lat: String?,
    val lng: String?,
    val storeHours: StoreHours?,
    val curbsideHours: StoreHours?,
    val status: String?
)

data class StoreHours(
    val sun: String,
    val mon: String,
    val tue: String,
    val wed: String,
    val thu: String,
    val fri: String,
    val sat: String
)
