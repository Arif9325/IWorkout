package com.example.iworkout.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.iworkout.ui.screens.Login
import com.example.iworkout.ui.screens.SignUp
import com.example.iworkout.ui.screens.Home
import com.example.iworkout.ui.screens.AddWorkout
import com.example.iworkout.ui.screens.EditWorkout
import com.example.iworkout.ui.screens.ViewWorkouts

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            Login(onSignUpNavigate = { navController.navigate("signup") },
                onLoginSuccess = { navController.navigate("home") })
        }
        composable("signup") {
            SignUp(onLoginNavigate = { navController.navigate("login") })
        }
        composable("home") {
            Home(navController)
        }
        composable(route = "add_workout") {
            AddWorkout(onWorkoutSuccess = { navController.navigate("home") },
                navController=navController )
        }
        composable(route = "view_workouts") {
            ViewWorkouts(onBack = { navController.popBackStack() },
                navController)
        }

        composable("edit_workout/{workoutId}") { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId")//?.toIntOrNull()
            if (workoutId != null) {
                EditWorkout(
                    workoutId = workoutId,
                    onBack = { navController.popBackStack() }
                )
            } else {
                // Handle the case where the workoutId is not valid or missing
                Text("Invalid Workout ID")
            }
        }
    }
}