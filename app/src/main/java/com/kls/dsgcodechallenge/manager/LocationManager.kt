package com.kls.dsgcodechallenge.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.kls.dsgcodechallenge.util.PermissionChecker
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationManager(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
        context
    ),
    private val permissionChecker: PermissionChecker
) {

    // Already checking permissions, adding suppress because warning doesn't apply.
    @SuppressLint("MissingPermission")
    suspend fun getLocation(): Location? {
        return withTimeoutOrNull(3000) {
            suspendCancellableCoroutine { continuation ->
                try {
                    val hasCoarsePermission = permissionChecker.hasLocationPermission()

                    if (hasCoarsePermission) {

                        val cancellationTokenSource = CancellationTokenSource()

                        // Permission already checked above, suppressing warning.
                        fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            cancellationTokenSource.token
                        )
                            .addOnSuccessListener { location ->
                                if (location != null) {
                                    // resume the coroutine with the location
                                    continuation.resume(location)
                                } else {
                                    // resume with null if no location found
                                    continuation.resume(null)
                                }
                            }
                            .addOnFailureListener { exception ->
                                // resume with an exception if failed
                                continuation.resumeWithException(exception)
                            }
                    } else {
                        // resume with null if permission is denied
                        continuation.resume(null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // resume with an exception in case of errors
                    continuation.resumeWithException(e)
                }
            }
        }
    }
}