/*
 * ViewModel for managing Road and Damage Report data.
 * This class handles the business logic, data stream transformation for the UI,
 * and communicates with the RoadRepository.
 */

package com.example.nammarasthehealth.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nammarasthehealth.data.DamageReport
import com.example.nammarasthehealth.data.Road
import com.example.nammarasthehealth.data.RoadRepository
import com.example.nammarasthehealth.data.TalukStats
import com.example.nammarasthehealth.data.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RoadViewModel(private val repository: RoadRepository) : ViewModel() {

    private val TAG = "RoadViewModel"

    // Exposed flow of all roads for the directory and map
    val allRoads: Flow<List<Road>> = repository.allRoads

    // State management for the currently logged-in user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    /**
     * Compute taluk-level statistics by observing the allRoads flow.
     * This transforms the raw road list into a grouped summary for the dashboard.
     */
    val talukStats: StateFlow<List<TalukStats>> = allRoads.map { roads ->
        if (roads.isEmpty()) return@map emptyList<TalukStats>()
        
        roads.groupBy { it.taluk }.map { (talukName, roadsInTaluk) ->
            val total = roadsInTaluk.size
            val critical = roadsInTaluk.count { it.healthScore < 50 }
            
            // Logic: Budget is an estimate based on number of roads
            val estimatedBudget = roadsInTaluk.size * 1.25 
            
            // Logic: Average health score determines efficiency
            val avgHealth = if (total > 0) roadsInTaluk.sumOf { it.healthScore } / total else 0
            
            TalukStats(
                talukName = talukName,
                totalRoads = total,
                criticalRoads = critical,
                budgetAllocated = "₹${"%.1f".format(estimatedBudget)} Cr",
                efficiency = avgHealth
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Prepare initial data on app startup
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            try {
                // Seed local DB if it's the first run
                val count = repository.getRoadCount()
                if (count == 0) {
                    Log.d(TAG, "Database empty. Seeding initial roads...")
                    seedInitialRoads()
                }
                
                // Fetch latest updates from the OSM AI service
                refreshRoadData()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize data: ${e.message}")
            }
        }
    }

    fun refreshRoadData() = viewModelScope.launch {
        repository.refreshRoadsFromApi()
    }

    fun getRoadById(id: Int): Flow<Road?> = repository.getRoadById(id)

    fun getReportsForRoad(roadId: Int): Flow<List<DamageReport>> = repository.getReportsForRoad(roadId)

    /**
     * Persist a new road project added by the user/admin
     */
    fun insertRoad(road: Road) = viewModelScope.launch {
        repository.insertRoad(road)
    }

    /**
     * Citizen submission of a damage report
     */
    fun reportDamage(report: DamageReport) = viewModelScope.launch {
        repository.insertDamageReport(report)
        Log.i(TAG, "New damage report submitted for road ID: ${report.roadId}")
    }

    // --- Authentication Flow ---

    fun signUp(user: User, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val success = repository.signUp(user)
        if (success) {
            _currentUser.value = user
            Log.d(TAG, "User signed up: ${user.email}")
        }
        onResult(success)
    }

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) = viewModelScope.launch {
        val user = repository.login(email, pass)
        if (user != null) {
            _currentUser.value = user
            Log.d(TAG, "Login successful for: $email")
        }
        onResult(user != null)
    }

    fun logout() {
        _currentUser.value = null
    }

    /**
     * Helper to populate the database with real-world road data for demo purposes
     */
    private suspend fun seedInitialRoads() {
        val demoRoads = listOf(
            Road(name = "Bannerghatta Main Road", contractorName = "BBMP Special Div", contractorContact = "080-22221111", warrantyPeriod = "24 Months", healthScore = 45, latitude = 12.9226, longitude = 77.5986, taluk = "Bangalore South"),
            Road(name = "Silk Board Junction", contractorName = "NHAI", contractorContact = "080-33334444", warrantyPeriod = "36 Months", healthScore = 30, latitude = 12.9175, longitude = 77.6233, taluk = "Bangalore East"),
            Road(name = "Outer Ring Road (HSR)", contractorName = "BDA", contractorContact = "080-44445555", warrantyPeriod = "48 Months", healthScore = 75, latitude = 12.9100, longitude = 77.6400, taluk = "Bangalore South"),
            Road(name = "Hosur Road", contractorName = "NHAI", contractorContact = "080-55556666", warrantyPeriod = "48 Months", healthScore = 85, latitude = 12.9000, longitude = 77.6500, taluk = "Anekal"),
            Road(name = "Mysore Road (RVCE)", contractorName = "PWD", contractorContact = "080-77778888", warrantyPeriod = "12 Months", healthScore = 60, latitude = 12.9400, longitude = 77.5500, taluk = "Kengeri")
        )
        demoRoads.forEach { repository.insertRoad(it) }
    }
}

/**
 * Factory needed to inject the repository into the ViewModel
 */
class RoadViewModelFactory(private val repository: RoadRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoadViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
