package com.cesae.dogbreeds.repository

import com.cesae.dogbreeds.data.api.ApiClient
import com.cesae.dogbreeds.data.db.AppDatabase
import com.cesae.dogbreeds.data.db.BreedNote
import com.cesae.dogbreeds.data.db.FavoriteBreed
import com.cesae.dogbreeds.data.model.Breed

class BreedsRepository(private val db: AppDatabase) {

    suspend fun fetchBreeds(): List<Breed> {
        val allBreeds = mutableListOf<Breed>()
        var page = 0
        while (true) {
            val results = ApiClient.dogApiService.getBreeds(limit = 100, page = page)
            if (results.isEmpty()) break
            allBreeds.addAll(results)
            if (results.size < 100) break
            page++
        }
        return allBreeds
    }

    // Favorites
    fun getFavorites() = db.favoriteBreedDao().getAll()
    suspend fun getFavoriteById(id: Int) = db.favoriteBreedDao().getById(id)
    suspend fun addFavorite(breed: FavoriteBreed) = db.favoriteBreedDao().insert(breed)
    suspend fun removeFavorite(id: Int) = db.favoriteBreedDao().deleteById(id)
    suspend fun updateFavoriteNote(id: Int, note: String) = db.favoriteBreedDao().updateNote(id, note)

    // Notes
    fun getNotes() = db.breedNoteDao().getAll()
    suspend fun getNoteById(id: Int) = db.breedNoteDao().getById(id)
    suspend fun saveNote(note: BreedNote) = db.breedNoteDao().insert(note)
    suspend fun deleteNote(id: Int) = db.breedNoteDao().deleteById(id)
}
