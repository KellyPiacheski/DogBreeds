package com.cesae.dogbreeds.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cesae.dogbreeds.data.db.AppDatabase
import com.cesae.dogbreeds.data.db.BreedNote
import com.cesae.dogbreeds.data.db.FavoriteBreed
import com.cesae.dogbreeds.data.model.Breed
import com.cesae.dogbreeds.repository.BreedsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BreedsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BreedsRepository(AppDatabase.getInstance(application))

    // Breeds list state
    private val _breeds = MutableStateFlow<List<Breed>>(emptyList())
    val breeds: StateFlow<List<Breed>> = _breeds

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Search & filter
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _selectedGroup = MutableStateFlow("Todos")
    val selectedGroup: StateFlow<String> = _selectedGroup

    val filteredBreeds: StateFlow<List<Breed>> = combine(_breeds, _searchText, _selectedGroup) { breeds, search, group ->
        breeds.filter { breed ->
            val matchesGroup = group == "Todos" || breed.breedGroup == group
            val matchesSearch = search.isBlank() ||
                breed.name.contains(search, ignoreCase = true) ||
                breed.breedGroup?.contains(search, ignoreCase = true) == true ||
                breed.origin?.contains(search, ignoreCase = true) == true
            matchesGroup && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groups: StateFlow<List<String>> = _breeds.map { breeds ->
        listOf("Todos") + breeds.mapNotNull { it.breedGroup }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Todos"))

    // Favorites & Notes
    val favorites: StateFlow<List<FavoriteBreed>> = repository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<BreedNote>> = repository.getNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadBreeds()
    }

    fun loadBreeds() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _breeds.value = repository.fetchBreeds()
            } catch (e: Exception) {
                _error.value = "Erro ao carregar raças: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSearchText(text: String) { _searchText.value = text }
    fun setSelectedGroup(group: String) { _selectedGroup.value = group }

    fun isFavorite(breedId: Int): Boolean = favorites.value.any { it.breedId == breedId }

    fun toggleFavorite(breed: Breed) {
        viewModelScope.launch {
            if (isFavorite(breed.id)) {
                repository.removeFavorite(breed.id)
            } else {
                repository.addFavorite(
                    FavoriteBreed(
                        breedId = breed.id,
                        name = breed.name,
                        imageUrl = breed.image?.url
                    )
                )
            }
        }
    }

    fun removeFavorite(id: Int) = viewModelScope.launch { repository.removeFavorite(id) }

    fun updateFavoriteNote(id: Int, note: String) =
        viewModelScope.launch { repository.updateFavoriteNote(id, note) }

    fun saveNote(breedId: Int, breedName: String, imageUrl: String?, note: String) {
        viewModelScope.launch {
            repository.saveNote(BreedNote(breedId, breedName, imageUrl, note))
        }
    }

    fun deleteNote(id: Int) = viewModelScope.launch { repository.deleteNote(id) }

    fun getNoteForBreed(breedId: Int): BreedNote? = notes.value.find { it.breedId == breedId }
}
