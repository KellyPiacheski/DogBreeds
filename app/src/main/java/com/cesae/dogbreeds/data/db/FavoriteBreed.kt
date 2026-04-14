package com.cesae.dogbreeds.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_breeds")
data class FavoriteBreed(
    @PrimaryKey val breedId: Int,
    val name: String,
    val imageUrl: String?,
    val note: String = "",
    val dateAdded: Long = System.currentTimeMillis()
)
