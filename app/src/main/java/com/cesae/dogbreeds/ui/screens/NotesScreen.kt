package com.cesae.dogbreeds.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cesae.dogbreeds.data.db.BreedNote
import com.cesae.dogbreeds.ui.navigation.Screen
import com.cesae.dogbreeds.viewmodel.BreedsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(viewModel: BreedsViewModel, navController: NavHostController) {
    val notes by viewModel.notes.collectAsState()
    var editingNote by remember { mutableStateOf<BreedNote?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Notas (${notes.size})") }) }
    ) { padding ->
        if (notes.isEmpty()) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Sem notas ainda.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes, key = { it.breedId }) { note ->
                    NoteItem(
                        note = note,
                        onDelete = { viewModel.deleteNote(note.breedId) },
                        onEdit = { editingNote = note },
                        onClick = { navController.navigate(Screen.BreedDetail.createRoute(note.breedId)) }
                    )
                }
            }
        }
    }

    editingNote?.let { note ->
        EditBreedNoteDialog(
            note = note,
            onDismiss = { editingNote = null },
            onSave = { updatedNote ->
                viewModel.saveNote(note.breedId, note.breedName, note.imageUrl, updatedNote)
                editingNote = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(
    note: BreedNote,
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
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = note.imageUrl,
                    contentDescription = note.breedName,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(note.breedName, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(Date(note.dateAdded))
                    Text(date, fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    Text(note.note, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun EditBreedNoteDialog(
    note: BreedNote,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(note.note) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar nota — ${note.breedName}") },
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
