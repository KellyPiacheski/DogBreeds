package com.cesae.dogbreeds.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cesae.dogbreeds.viewmodel.BreedsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedDetailScreen(breedId: Int, viewModel: BreedsViewModel, navController: NavHostController) {
    val breeds by viewModel.breeds.collectAsState()
    val breed = breeds.find { it.id == breedId } ?: return
    val favorites by viewModel.favorites.collectAsState()
    val isFav = favorites.any { it.breedId == breedId }
    val notes by viewModel.notes.collectAsState()
    val note = notes.find { it.breedId == breedId }
    var showNoteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(breed.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { showNoteDialog = true }) {
                        Icon(Icons.Default.Note, contentDescription = "Nota")
                    }
                    IconButton(onClick = { viewModel.toggleFavorite(breed) }) {
                        Icon(
                            if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFav) MaterialTheme.colorScheme.error else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = breed.image?.url,
                contentDescription = breed.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(label = "Grupo", value = breed.breedGroup)
                InfoRow(label = "Origem", value = breed.origin)
                InfoRow(label = "Esperança de vida", value = breed.lifeSpan)
                InfoRow(label = "Peso (Imperial)", value = breed.weight?.imperial)
                InfoRow(label = "Peso (Métrico)", value = breed.weight?.metric)
                InfoRow(label = "Altura (Imperial)", value = breed.height?.imperial)
                InfoRow(label = "Altura (Métrica)", value = breed.height?.metric)
                InfoRow(label = "Criado para", value = breed.bredFor)
                InfoRow(label = "Temperamento", value = breed.temperament)
                if (note != null && note.note.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text("Nota pessoal", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(4.dp))
                    Text(note.note, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (showNoteDialog) {
        AddNoteDialog(breed = breed, viewModel = viewModel, onDismiss = { showNoteDialog = false })
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
