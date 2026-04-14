package com.cesae.dogbreeds.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breed_notes")
data class BreedNote(
    @PrimaryKey val breedId: Int,
    val breedName: String,
    val imageUrl: String?,
    val note: String,
    val dateAdded: Long = System.currentTimeMillis()
)
