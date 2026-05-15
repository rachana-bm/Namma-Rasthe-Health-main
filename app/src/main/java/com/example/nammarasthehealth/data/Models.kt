/*
 * Data Models for Namma-Raste
 * Defines the core entities used across the app, including Room database tables
 * and UI state objects.
 */

package com.example.nammarasthehealth.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a registered citizen or admin user.
 * Unique index on email ensures no duplicate accounts.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val taluk: String = "Not Specified"
)

/**
 * Core entity representing a road project. 
 * 'unique = true' on name prevents accidental duplicate entries.
 */
@Entity(
    tableName = "roads",
    indices = [Index(value = ["name"], unique = true)]
)
data class Road(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val contractorName: String,
    val contractorContact: String,
    val warrantyPeriod: String,
    val healthScore: Int = 100, // 0-100 scale
    val latitude: Double,
    val longitude: Double,
    val taluk: String = "District HQ"
)

/**
 * UI-only model used to display aggregate stats on the dashboard.
 */
data class TalukStats(
    val talukName: String,
    val totalRoads: Int,
    val criticalRoads: Int,
    val budgetAllocated: String,
    val efficiency: Int
)

/**
 * Represents a damage report submitted by a citizen.
 * Includes optional photo data as a byte array for local storage.
 */
@Entity(tableName = "damage_reports")
data class DamageReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roadId: Int,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double,
    val longitude: Double,
    val photoData: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DamageReport

        if (id != other.id) return false
        if (roadId != other.roadId) return false
        if (description != other.description) return false
        if (timestamp != other.timestamp) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (photoData != null) {
            if (other.photoData == null) return false
            if (!photoData.contentEquals(other.photoData)) return false
        } else if (other.photoData != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + roadId
        result = 31 * result + description.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + (photoData?.contentHashCode() ?: 0)
        return result
    }
}
