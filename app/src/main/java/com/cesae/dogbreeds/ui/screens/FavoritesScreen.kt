package com.cesae.dogbreeds.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cesae.dogbreeds.data.db.FavoriteBreed
import com.cesae.dogbreeds.ui.navigation.Screen
import com.cesae.dogbreeds.viewmodel.BreedsViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(viewModel: BreedsViewModel, navController: NavHostController) {
    val favorites by viewModel.favorites.collectAsState()
    var editingFavorite by remember { mutableStateOf<FavoriteBreed?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Favoritos (${favorites.size})") }) }
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Sem raças favoritas ainda.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites, key = { it.breedId }) { fav ->
                    FavoriteItem(
                        favorite = fav,
                        onDelete = { viewModel.removeFavorite(fav.breedId) },
                        onEdit = { editingFavorite = fav },
                        onClick = { navController.navigate(Screen.BreedDetail.createRoute(fav.breedId)) }
                    )
                }
            }
        }
    }

    editingFavorite?.let { fav ->
        EditFavoriteNoteDialog(
            favorite = fav,
            onDismiss = { editingFavorite = null },
            onSave = { note ->
                viewModel.updateFavoriteNote(fav.breedId, note)
                editingFavorite = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteItem(
    favorite: FavoriteBreed,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onClick: () -> Unit = {}
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> { onDelete(); true }
                SwipeToDismissBoxValue.StartToEnd -> { onEdit(); false }
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
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                } else {
                    Icon(Icons.Default.Edit, contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = favorite.imageUrl,
                    contentDescription = favorite.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(favorite.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(Date(favorite.dateAdded))
                    Text(date, fontSize = 12.sp, color = Color.Gray)
                    if (favorite.note.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(favorite.note, fontSize = 13.sp, maxLines = 2)
                    }
                }
            }
        }
    }
}

@Composable
fun EditFavoriteNoteDialog(
    favorite: FavoriteBreed,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(favorite.note) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar nota — ${favorite.name}") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.length <= 300) text = it },
                label = { Text("Nota (${text.length}/300)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
        },
        confirmButton = { TextButton(onClick = { onSave(text) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
