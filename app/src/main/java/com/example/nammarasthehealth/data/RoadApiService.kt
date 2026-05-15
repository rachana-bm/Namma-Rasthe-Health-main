/*
 * RoadApiService.kt
 * Retrofit interface for interacting with the Namma-Raste backend/OSM AI service.
 */

package com.example.nammarasthehealth.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Data transfer object for road data coming from the API.
 */
data class RoadApiResource(
    val id: Int? = null,
    val name: String,
    val contractor: String,
    val contact: String,
    val warranty: String,
    val health: Int,
    val lat: Double,
    val lng: Double,
    val taluk: String? = null
)

/**
 * Request body for submitting damage reports to the server.
 */
data class DamageReportRequest(
    val roadId: Int,
    val description: String,
    val lat: Double,
    val lng: Double,
    val timestamp: Long
)

/**
 * Retrofit API definition.
 */
interface RoadApiService {

    @GET("worst-roads.json")
    suspend fun getWorstRoads(): List<RoadApiResource>

    @POST("report-damage")
    suspend fun postDamageReport(@Body report: DamageReportRequest)

    companion object {
        // Real-time OSM AI service endpoint
        private const val BASE_URL = "https://api.nammaraste-osm.ai/v1/"

        fun create(): RoadApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RoadApiService::class.java)
        }
    }
}
