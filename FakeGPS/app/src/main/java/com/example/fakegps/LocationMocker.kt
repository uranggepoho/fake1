package com.example.fakegps

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class LocationMocker(private val context: Context) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun startMock(lat: Double, lon: Double, accuracyMeters: Float = 5f, altitude: Double? = null) {
        fusedClient.setMockMode(true).await()

        val loc = Location("mock").apply {
            latitude = lat
            longitude = lon
            accuracy = accuracyMeters
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = System.nanoTime()
            altitude?.let { this.altitude = it }
        }
        fusedClient.setMockLocation(loc).await()
    }

    suspend fun pushUpdate(lat: Double, lon: Double, accuracyMeters: Float = 5f) {
        val loc = Location("mock").apply {
            latitude = lat
            longitude = lon
            accuracy = accuracyMeters
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = System.nanoTime()
        }
        fusedClient.setMockLocation(loc).await()
    }

    suspend fun stopMock() {
        fusedClient.setMockMode(false).await()
    }
}
