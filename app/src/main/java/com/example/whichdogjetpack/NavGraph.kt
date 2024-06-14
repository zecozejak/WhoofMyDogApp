package com.example.whichdogjetpack

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("encyclopedia") { DogEncyclopediaScreen(navController) }
        composable("recognize") { RecognizeDogScreen(navController) }
        composable("mydogs") { MyDogsScreen(navController) }
    }
}
