package com.cesae.dogbreeds.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedNoteDao {
    @Query("SELECT * FROM breed_notes ORDER BY dateAdded DESC")
    fun getAll(): Flow<List<BreedNote>>

    @Query("SELECT * FROM breed_notes WHERE breedId = :id")
    suspend fun getById(id: Int): BreedNote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: BreedNote)

    @Delete
    suspend fun delete(note: BreedNote)

    @Query("DELETE FROM breed_notes WHERE breedId = :id")
    suspend fun deleteById(id: Int)
}
