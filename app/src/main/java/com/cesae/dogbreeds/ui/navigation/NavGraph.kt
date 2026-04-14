package com.cesae.dogbreeds.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cesae.dogbreeds.ui.screens.*
import com.cesae.dogbreeds.viewmodel.BreedsViewModel

sealed class Screen(val route: String) {
    object BreedsList : Screen("breeds_list")
    object BreedDetail : Screen("breed_detail/{breedId}") {
        fun createRoute(breedId: Int) = "breed_detail/$breedId"
    }
    object Favorites : Screen("favorites")
    object Notes : Screen("notes")
    object Quiz : Screen("quiz")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: BreedsViewModel
) {
    NavHost(navController = navController, startDestination = Screen.BreedsList.route) {
        composable(Screen.BreedsList.route) {
            BreedsListScreen(viewModel = viewModel, navController = navController)
        }
        composable(
            route = Screen.BreedDetail.route,
            arguments = listOf(navArgument("breedId") { type = NavType.IntType })
        ) { backStackEntry ->
            val breedId = backStackEntry.arguments?.getInt("breedId") ?: return@composable
            BreedDetailScreen(breedId = breedId, viewModel = viewModel, navController = navController)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Notes.route) {
            NotesScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.Quiz.route) {
            QuizScreen(viewModel = viewModel, navController = navController)
        }
    }
}
