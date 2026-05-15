/*
 * NammaRasteApplication.kt
 * Custom Application class for dependency injection and global state management.
 */

package com.example.nammarasthehealth

import android.app.Application
import com.example.nammarasthehealth.data.AppDatabase
import com.example.nammarasthehealth.data.RoadApiService
import com.example.nammarasthehealth.data.RoadRepository

class NammaRasteApplication : Application() {
    
    // We use lazy initialization to ensure the database and API services are 
    // only created when they are actually needed.
    
    private val database by lazy { 
        AppDatabase.getDatabase(this) 
    }
    
    private val apiService by lazy { 
        RoadApiService.create() 
    }
    
    // The repository is the single point of contact for the ViewModels
    val repository by lazy { 
        RoadRepository(database.roadDao(), apiService) 
    }
    
    override fun onCreate() {
        super.onCreate()
        // App-wide initialization can go here
    }
}
