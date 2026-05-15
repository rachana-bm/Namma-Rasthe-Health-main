/*
 * Data Access Object (DAO) for the Namma-Raste application.
 * Defines the SQL queries and maps them to method calls for the Room database.
 */

package com.example.nammarasthehealth.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RoadDao {

    // --- Road Related Operations ---

    @Query("SELECT * FROM roads ORDER BY name ASC")
    fun getAllRoads(): Flow<List<Road>>

    @Query("SELECT COUNT(*) FROM roads")
    suspend fun getRoadCount(): Int

    @Query("SELECT * FROM roads WHERE id = :roadId")
    fun getRoadById(roadId: Int): Flow<Road?>

    @Query("SELECT * FROM roads WHERE id = :roadId")
    suspend fun getRoadByIdOnce(roadId: Int): Road?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoad(road: Road)

    @Update
    suspend fun updateRoad(road: Road)


    // --- Damage Report Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDamageReport(report: DamageReport)

    @Query("SELECT * FROM damage_reports WHERE roadId = :roadId ORDER BY timestamp DESC")
    fun getReportsForRoad(roadId: Int): Flow<List<DamageReport>>

    @Query("SELECT * FROM damage_reports WHERE roadId = :roadId")
    suspend fun getReportsForRoadOnce(roadId: Int): List<DamageReport>


    // --- User/Auth Operations ---

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?
}
