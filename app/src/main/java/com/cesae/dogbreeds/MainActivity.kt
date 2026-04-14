package com.cesae.dogbreeds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cesae.dogbreeds.ui.navigation.AppNavGraph
import com.cesae.dogbreeds.ui.navigation.Screen
import com.cesae.dogbreeds.ui.theme.DogBreedsTheme
import com.cesae.dogbreeds.viewmodel.BreedsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DogBreedsTheme {
                DogBreedsAppContent()
            }
        }
    }
}

@Composable
fun DogBreedsAppContent() {
    val navController = rememberNavController()
    val viewModel: BreedsViewModel = viewModel()
    val favorites by viewModel.favorites.collectAsState()
    val notes by viewModel.notes.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = listOf(
        Screen.BreedsList.route,
        Screen.Favorites.route,
        Screen.Notes.route,
        Screen.Quiz.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.BreedsList.route,
                        onClick = {
                            navController.navigate(Screen.BreedsList.route) {
                                popUpTo(Screen.BreedsList.route) { inclusive = true }
                            }
                        },
                        icon = { Icon(Icons.Default.Pets, contentDescription = null) },
                        label = { Text("Raças") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Favorites.route,
                        onClick = {
                            navController.navigate(Screen.Favorites.route) {
                                popUpTo(Screen.BreedsList.route)
                            }
                        },
                        icon = {
                            BadgedBox(badge = {
                                if (favorites.isNotEmpty()) Badge { Text("${favorites.size}") }
                            }) {
                                Icon(Icons.Default.Favorite, contentDescription = null)
                            }
                        },
                        label = { Text("Favoritos") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Notes.route,
                        onClick = {
                            navController.navigate(Screen.Notes.route) {
                                popUpTo(Screen.BreedsList.route)
                            }
                        },
                        icon = {
                            BadgedBox(badge = {
                                if (notes.isNotEmpty()) Badge { Text("${notes.size}") }
                            }) {
                                Icon(Icons.Default.Note, contentDescription = null)
                            }
                        },
                        label = { Text("Notas") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Quiz.route,
                        onClick = {
                            navController.navigate(Screen.Quiz.route) {
                                popUpTo(Screen.BreedsList.route)
                            }
                        },
                        icon = { Icon(Icons.Default.Star, contentDescription = null) },
                        label = { Text("Recomendação") }
                    )
                }
            }
        }
    ) { _ ->
        AppNavGraph(
            navController = navController,
            viewModel = viewModel
        )
    }
}
