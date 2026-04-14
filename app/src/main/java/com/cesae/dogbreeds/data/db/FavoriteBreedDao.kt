package com.cesae.dogbreeds.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteBreedDao {
    @Query("SELECT * FROM favorite_breeds ORDER BY dateAdded DESC")
    fun getAll(): Flow<List<FavoriteBreed>>

    @Query("SELECT * FROM favorite_breeds WHERE breedId = :id")
    suspend fun getById(id: Int): FavoriteBreed?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(breed: FavoriteBreed)

    @Delete
    suspend fun delete(breed: FavoriteBreed)

    @Query("DELETE FROM favorite_breeds WHERE breedId = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE favorite_breeds SET note = :note WHERE breedId = :id")
    suspend fun updateNote(id: Int, note: String)
}
