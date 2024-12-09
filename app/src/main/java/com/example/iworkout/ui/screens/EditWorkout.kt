package com.example.iworkout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iworkout.data.model.Workout
import com.example.iworkout.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWorkout(
    workoutId: String,
    onBack: () -> Unit, // This will be used to navigate back after saving/canceling
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    var workout by remember { mutableStateOf<Workout?>(null) }
    var workoutName by remember { mutableStateOf("") }
    var exerciseType by remember { mutableStateOf("") }
    var selectedReps by remember { mutableStateOf("") }
    var selectedSets by remember { mutableStateOf("") }
    val isLoading = workout == null // Flag for loading state

    // Options for Sets and Reps
    val setsOptions = (1..10).map { it.toString() } // Set options from 1 to 10
    val repsOptions = (1..15).map { it.toString() } // Rep options from 1 to 15

    // Fetch workout details when screen is first displayed
    LaunchedEffect(workoutId) {
        workoutViewModel.getWorkoutById(workoutId) { fetchedWorkout ->
            if (fetchedWorkout != null) {
                workout = fetchedWorkout
                workoutName = fetchedWorkout.workoutName
                exerciseType = fetchedWorkout.exerciseType
                selectedReps = fetchedWorkout.reps.toString()
                selectedSets = fetchedWorkout.sets.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Workout", color = Color.White) },
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Workout Name input
                OutlinedTextField(
                    value = workoutName,
                    onValueChange = { workoutName = it },
                    label = { Text("Workout Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Exercise Type input
                OutlinedTextField(
                    value = exerciseType,
                    onValueChange = { exerciseType = it },
                    label = { Text("Exercise Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Reps Dropdown
                DropdownMenuWithLabel(
                    label = "Reps",
                    options = repsOptions,
                    selectedOption = selectedReps,
                    onOptionSelected = { selectedReps = it }
                )

                // Sets Dropdown
                DropdownMenuWithLabel(
                    label = "Sets",
                    options = setsOptions,
                    selectedOption = selectedSets,
                    onOptionSelected = { selectedSets = it }
                )

                // Save Button
                Button(
                    onClick = {
                        val updatedWorkout = workout?.copy(
                            workoutName = workoutName,
                            exerciseType = exerciseType,
                            reps = selectedReps.toIntOrNull() ?: 0,
                            sets = selectedSets.toIntOrNull() ?: 0
                        )
                        if (updatedWorkout != null) {
                            workoutViewModel.updateWorkout(updatedWorkout) { success ->
                                if (success) {
                                    onBack() // Go back on successful save
                                } else {
                                    // Handle save failure, show error message
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }

                // Cancel Button
                Button(
                    onClick = { onBack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

