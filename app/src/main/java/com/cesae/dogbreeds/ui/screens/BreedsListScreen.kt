package com.cesae.dogbreeds.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.cesae.dogbreeds.data.model.Breed
import com.cesae.dogbreeds.ui.components.BreedRowItem
import com.cesae.dogbreeds.ui.navigation.Screen
import com.cesae.dogbreeds.viewmodel.BreedsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedsListScreen(viewModel: BreedsViewModel, navController: NavHostController) {
    val breeds by viewModel.filteredBreeds.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val selectedGroup by viewModel.selectedGroup.collectAsState()

    val favorites by viewModel.favorites.collectAsState()
    var addNoteBreed by remember { mutableStateOf<Breed?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Raças de Cães") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::setSearchText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Pesquisar raças...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Group filter chips
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groups.forEach { group ->
                    FilterChip(
                        selected = group == selectedGroup,
                        onClick = { viewModel.setSelectedGroup(group) },
                        label = { Text(group) }
                    )
                }
            }

            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error ?: "Erro desconhecido", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = viewModel::loadBreeds) { Text("Tentar novamente") }
                    }
                }
                breeds.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma raça encontrada.")
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(breeds, key = { it.id }) { breed ->
                        val isFav = favorites.any { it.breedId == breed.id }
                        SwipeToDismissBreedItem(
                            breed = breed,
                            isFavorite = isFav,
                            onFavoriteToggle = { viewModel.toggleFavorite(breed) },
                            onAddNote = { addNoteBreed = breed },
                            onClick = { navController.navigate(Screen.BreedDetail.createRoute(breed.id)) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }

    addNoteBreed?.let { breed ->
        AddNoteDialog(
            breed = breed,
            viewModel = viewModel,
            onDismiss = { addNoteBreed = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissBreedItem(
    breed: Breed,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onAddNote: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> { onFavoriteToggle(); false }
                SwipeToDismissBoxValue.StartToEnd -> { onAddNote(); false }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (direction == SwipeToDismissBoxValue.EndToStart)
                    Arrangement.End else Arrangement.Start
            ) {
                if (direction == SwipeToDismissBoxValue.EndToStart) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(Icons.Default.Note, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    ) {
        Surface(onClick = onClick, color = MaterialTheme.colorScheme.surface) {
            BreedRowItem(breed = breed)
        }
    }
}

@Composable
fun AddNoteDialog(
    breed: Breed,
    viewModel: BreedsViewModel,
    onDismiss: () -> Unit
) {
    var noteText by remember { mutableStateOf(viewModel.getNoteForBreed(breed.id)?.note ?: "") }
    var addToFavorites by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    val isFav = viewModel.isFavorite(breed.id)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nota para ${breed.name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = {
                        if (it.length <= 300) noteText = it
                        if (it.isNotBlank()) showError = false
                    },
                    label = { Text("Nota (${noteText.length}/300)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("A nota não pode estar vazia.", color = MaterialTheme.colorScheme.error) }
                    } else null
                )
                if (!isFav) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = addToFavorites, onCheckedChange = { addToFavorites = it })
                        Text("Adicionar aos favoritos")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (noteText.isBlank()) {
                    showError = true
                } else {
                    viewModel.saveNote(breed.id, breed.name, breed.image?.url, noteText)
                    if (addToFavorites && !isFav) viewModel.toggleFavorite(breed)
                    onDismiss()
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
