/*
 * AppDatabase.kt
 * Room database configuration for the Namma-Raste app.
 * Handles persistent storage for Roads, Damage Reports, and Users.
 */

package com.example.nammarasthehealth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Version 5: Added unique index for road names to prevent duplicates
@Database(
    entities = [Road::class, DamageReport::class, User::class], 
    version = 5, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    // DAO accessors
    abstract fun roadDao(): RoadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Singleton pattern for the database instance.
         * Destructive migration is enabled for dev/demo simplicity.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "namma_raste_db" // Using a descriptive name
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
