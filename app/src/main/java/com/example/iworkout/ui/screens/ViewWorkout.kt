package com.example.iworkout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iworkout.data.model.Workout
import com.example.iworkout.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ViewWorkouts(
    onBack: () -> Unit,
    navController: NavController,
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val workoutsState = remember { mutableStateOf<List<Workout>>(emptyList()) }
    val selectedDayId = remember { mutableStateOf("") } // Default to empty string for `dayId`
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val daysOfWeek = mapOf(
        "Monday" to "1", "Tuesday" to "2", "Wednesday" to "3",
        "Thursday" to "4", "Friday" to "5", "Saturday" to "6", "Sunday" to "7"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        // Day Selection Dropdown
        Button(onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)) {
            Text("Back")
        }
        DropdownMenuWithLabel(
            label = "Day of the Week",
            options = daysOfWeek.keys.toList(),
            selectedOption = daysOfWeek.entries.find { it.value == selectedDayId.value }?.key ?: "",
            onOptionSelected = { day ->
                selectedDayId.value = daysOfWeek[day] ?: ""

                // Fetch workouts for the selected day
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    workoutViewModel.getWorkoutsForDay(userId, selectedDayId.value) { fetchedWorkouts ->
                        workoutsState.value = fetchedWorkouts
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("User ID not found!")
                    }
                }
            }
        )

        // Workouts List
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(workoutsState.value) { workout ->
                WorkoutItem(workout = workout, navController)
            }
        }

        // Snackbar Host for error messages
        SnackbarHost(hostState = snackbarHostState)

    }
}

@Composable
fun WorkoutItem(workout: Workout, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Workout Name: ${workout.workoutName}")
            Text("Exercise Type: ${workout.exerciseType}")
            Text("Reps: ${workout.reps}, Sets: ${workout.sets}")
        }
        Button(
            onClick = {
                var workoutId = workout.workoutId

                // Assuming `workoutId` is available
                navController.navigate("edit_workout/" + workoutId)
            }
        ) {
            Text("Edit Workout")
        }
    }
}
