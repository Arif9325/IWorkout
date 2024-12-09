package com.example.iworkout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iworkout.data.model.Workout
import com.example.iworkout.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
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

    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("View Workouts", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Days of the Week Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                daysOfWeek.forEachIndexed { index, day ->
                    Button(
                        onClick = {
                            selectedDayId.value = (index + 1).toString()
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
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedDayId.value == (index + 1).toString())
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = day,
                            color = if (selectedDayId.value == (index + 1).toString()) Color.White else Color.Black,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Workouts List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(workoutsState.value) { workout ->
                    WorkoutItem(workout = workout, navController,
                        onDelete = { workoutToDelete ->
                            workoutsState.value -= workoutToDelete
                            // Call the delete function and show snackbar in the onComplete callback
                            workoutViewModel.deleteWorkout(workoutToDelete) { success ->
                                coroutineScope.launch {
                                    if (success) {
                                        snackbarHostState.showSnackbar("Workout deleted.")
                                    } else {
                                        snackbarHostState.showSnackbar("Failed to delete workout.")
                                    }
                                }
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun WorkoutItem(workout: Workout, navController: NavController, onDelete: (Workout) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween // Ensure space between text and buttons
    ) {
        // Text content on the left side
        Column(
            modifier = Modifier.weight(1f) // Allow this to take up as much space as possible
        ) {
            Text("Workout Name: ${workout.workoutName}")
            Text("Exercise Type: ${workout.exerciseType}")
            Text("Reps: ${workout.reps}, Sets: ${workout.sets}")
        }

        // Buttons on the right side
        Row {
            // Edit Button (left of Delete)
            Button(
                onClick = {
                    val workoutId = workout.workoutId
                    navController.navigate("edit_workout/$workoutId")
                },
                modifier = Modifier.padding(end = 8.dp)  // Space between Edit and Delete
            ) {
                Text("Edit Workout")
            }

            // Delete Button (right of Edit)
            Button(
                onClick = { onDelete(workout) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        }
    }
}

