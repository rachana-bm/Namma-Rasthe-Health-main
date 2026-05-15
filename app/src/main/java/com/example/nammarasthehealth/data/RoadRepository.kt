/*
 * Repository class that acts as the single source of truth for Road data.
 * It coordinates data between the local Room database (RoadDao) and the 
 * remote OSM AI service (RoadApiService).
 */

package com.example.nammarasthehealth.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class RoadRepository(
    private val roadDao: RoadDao,
    private val apiService: RoadApiService
) {
    private val TAG = "RoadRepository"

    // Exposed data streams
    val allRoads: Flow<List<Road>> = roadDao.getAllRoads()

    suspend fun getRoadCount(): Int = roadDao.getRoadCount()

    fun getRoadById(id: Int): Flow<Road?> = roadDao.getRoadById(id)

    fun getReportsForRoad(roadId: Int): Flow<List<DamageReport>> = roadDao.getReportsForRoad(roadId)

    /**
     * Inserts or updates a road project in the local database.
     */
    suspend fun insertRoad(road: Road) {
        roadDao.insertRoad(road)
    }

    /**
     * Fetches the latest road health data from the external OSM AI API
     * and synchronizes it with our local storage.
     */
    suspend fun refreshRoadsFromApi() {
        try {
            Log.d(TAG, "Fetching updates from Road API...")
            val apiRoads = apiService.getWorstRoads()
            
            apiRoads.forEach { data ->
                val road = Road(
                    name = data.name,
                    contractorName = data.contractor,
                    contractorContact = data.contact,
                    warrantyPeriod = data.warranty,
                    healthScore = data.health,
                    latitude = data.lat,
                    longitude = data.lng,
                    taluk = data.taluk ?: "District HQ"
                )
                roadDao.insertRoad(road)
            }
            Log.i(TAG, "Successfully synced ${apiRoads.size} roads from API.")
        } catch (e: Exception) {
            Log.e(TAG, "API Sync failed: ${e.message}")
        }
    }

    /**
     * Handles the submission of a new damage report.
     * Saves locally for offline support and attempts to push to the cloud.
     */
    suspend fun insertDamageReport(report: DamageReport) {
        // 1. Save locally
        roadDao.insertDamageReport(report)
        
        // 2. Update the local health score heuristic
        updateRoadHealth(report.roadId)
        
        // 3. Push to the real-time service
        try {
            val request = DamageReportRequest(
                roadId = report.roadId,
                description = report.description,
                lat = report.latitude,
                lng = report.longitude,
                timestamp = report.timestamp
            )
            apiService.postDamageReport(request)
            Log.d(TAG, "Damage report pushed to cloud for Road ID: ${report.roadId}")
        } catch (e: Exception) {
            // Note: In a real app, we might queue this for a background worker/sync
            Log.w(TAG, "Cloud sync failed (offline?), stored locally: ${e.message}")
        }
    }

    /**
     * A simple local calculation to adjust road health based on reported issues.
     */
    private suspend fun updateRoadHealth(roadId: Int) {
        val road = roadDao.getRoadByIdOnce(roadId)
        val reports = roadDao.getReportsForRoadOnce(roadId)
        
        if (road != null) {
            // Heuristic: Each report impacts health by roughly 10 points
            val penalty = reports.size * 10
            val newScore = (100 - penalty).coerceAtLeast(0)
            
            roadDao.updateRoad(road.copy(healthScore = newScore))
            Log.v(TAG, "Updated Road ${road.name} health to $newScore%")
        }
    }

    // --- User Management ---

    suspend fun signUp(user: User): Boolean {
        return try {
            roadDao.insertUser(user)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Signup failed for ${user.email}: ${e.message}")
            false
        }
    }

    suspend fun login(email: String, password: String): User? {
        val user = roadDao.getUserByEmail(email)
        return if (user?.password == password) {
            Log.d(TAG, "User $email authenticated.")
            user
        } else {
            Log.w(TAG, "Authentication failed for $email.")
            null
        }
    }
}
