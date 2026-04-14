package com.cesae.dogbreeds.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.cesae.dogbreeds.data.model.Breed
import com.cesae.dogbreeds.ui.navigation.Screen
import com.cesae.dogbreeds.viewmodel.BreedsViewModel

data class QuizQuestion(val question: String, val options: List<String>)

val quizQuestions = listOf(
    QuizQuestion(
        "Qual é o teu estilo de vida?",
        listOf("Muito ativo / Desporto", "Moderado / Passeios diários", "Tranquilo / Casa")
    ),
    QuizQuestion(
        "Qual é o teu espaço?",
        listOf("Casa com jardim grande", "Apartamento médio", "Espaço pequeno")
    ),
    QuizQuestion(
        "Preferes cães de que tamanho?",
        listOf("Grande (>25kg)", "Médio (10-25kg)", "Pequeno (<10kg)")
    ),
    QuizQuestion(
        "Tens crianças em casa?",
        listOf("Sim, crianças pequenas", "Sim, crianças mais velhas", "Não")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: BreedsViewModel, navController: NavHostController) {
    val breeds by viewModel.breeds.collectAsState()
    var currentQuestion by remember { mutableIntStateOf(0) }
    val answers = remember { mutableStateListOf<Int>() }
    var showResults by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recomendação") },
                navigationIcon = {
                    if (currentQuestion > 0 && !showResults) {
                        IconButton(onClick = {
                            answers.removeLastOrNull()
                            currentQuestion--
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (!showResults) {
                // Progress
                LinearProgressIndicator(
                    progress = { currentQuestion.toFloat() / quizQuestions.size },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Pergunta ${currentQuestion + 1} de ${quizQuestions.size}",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(Modifier.height(24.dp))

                val q = quizQuestions[currentQuestion]
                Text(q.question, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(24.dp))

                q.options.forEachIndexed { index, option ->
                    Button(
                        onClick = {
                            answers.add(index)
                            if (currentQuestion < quizQuestions.size - 1) {
                                currentQuestion++
                            } else {
                                showResults = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(option, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            } else {
                val recommended = getRecommendedBreeds(breeds, answers)
                Text("Raças recomendadas para ti",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(recommended) { breed ->
                        Card(
                            onClick = { navController.navigate(Screen.BreedDetail.createRoute(breed.id)) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = breed.image?.url,
                                    contentDescription = breed.name,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(breed.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    breed.breedGroup?.let {
                                        Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                answers.clear()
                                currentQuestion = 0
                                showResults = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Recomeçar") }
                    }
                }
            }
        }
    }
}

fun getRecommendedBreeds(breeds: List<Breed>, answers: List<Int>): List<Breed> {
    if (breeds.isEmpty() || answers.size < 4) return emptyList()

    val lifestyle = answers[0]  // 0=very active, 1=moderate, 2=calm
    val space = answers[1]      // 0=big garden, 1=medium apt, 2=small
    val size = answers[2]       // 0=large, 1=medium, 2=small
    val children = answers[3]   // 0=young kids, 1=older kids, 2=no kids

    val activeGroups = listOf("Sporting", "Herding", "Working")
    val calmGroups = listOf("Toy", "Non-Sporting")
    val familyKeywords = listOf("gentle", "friendly", "patient", "loyal", "affectionate")

    return breeds.map { breed ->
        var score = 0
        val group = breed.breedGroup ?: ""
        val temperament = breed.temperament?.lowercase() ?: ""
        val weightMetric = breed.weight?.metric?.split("-")?.lastOrNull()?.trim()?.toIntOrNull() ?: 15

        // Lifestyle scoring
        when (lifestyle) {
            0 -> if (activeGroups.any { group.contains(it) }) score += 3
            1 -> score += 1
            2 -> if (calmGroups.any { group.contains(it) }) score += 3
        }

        // Size scoring
        when (size) {
            0 -> if (weightMetric > 25) score += 3
            1 -> if (weightMetric in 10..25) score += 3
            2 -> if (weightMetric < 10) score += 3
        }

        // Space
        when (space) {
            0 -> score += 1
            1 -> if (weightMetric <= 25) score += 2
            2 -> if (weightMetric < 12) score += 3
        }

        // Children
        if (children < 2) {
            familyKeywords.forEach { kw -> if (temperament.contains(kw)) score += 1 }
        }

        breed to score
    }
        .sortedByDescending { it.second }
        .take(5)
        .map { it.first }
}
