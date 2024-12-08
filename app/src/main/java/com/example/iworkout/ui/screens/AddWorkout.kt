package com.example.iworkout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AddWorkout(onWorkoutSuccess: () -> Unit,
               workoutViewModel: WorkoutViewModel = viewModel()
) {
    var selectedDayId by remember { mutableStateOf(0) }
    var workoutName by remember { mutableStateOf("") }
    var exerciseType by remember { mutableStateOf("") }
    var selectedReps by remember { mutableStateOf(0) }
    var selectedSets by remember { mutableStateOf(0) }

    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val daysOfWeek = mapOf(
        "Monday" to 1, "Tuesday" to 2, "Wednesday" to 3,
        "Thursday" to 4, "Friday" to 5, "Saturday" to 6, "Sunday" to 7
    )
    val repsOptions = (1..20).toList()
    val setsOptions = (1..10).toList()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Success") },
            text = { Text("Workout was successfully added!") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onWorkoutSuccess()  // Trigger success callback after dismissing dialog
                }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Day of the Week Dropdown
            DropdownMenuWithLabel(
                label = "Day of the Week",
                options = daysOfWeek.keys.toList(),
                selectedOption = daysOfWeek.entries.find { it.value == selectedDayId }?.key ?: "",
                onOptionSelected = { selectedDayId = daysOfWeek[it] ?: 0 }  // Set as Int
            )

            // Workout Name Input
            OutlinedTextField(
                value = workoutName,
                onValueChange = { workoutName = it },
                label = { Text("Workout Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Exercise Type Input
            OutlinedTextField(
                value = exerciseType,
                onValueChange = { exerciseType = it },
                label = { Text("Exercise Type") },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenuWithLabel(
                label = "Number of Reps",
                options = repsOptions.map { it.toString() },
                selectedOption = if (selectedReps == 0) "" else selectedReps.toString(),
                onOptionSelected = { selectedReps = it.toInt() }
            )

            // Sets Dropdown
            DropdownMenuWithLabel(
                label = "Number of Sets",
                options = setsOptions.map { it.toString() },
                selectedOption = if (selectedSets == 0) "" else selectedSets.toString(),
                onOptionSelected = { selectedSets = it.toInt() }
            )
            Button(
                onClick = {
                    // Check that all fields are filled and valid
                    if (selectedDayId != 0 && workoutName.isNotBlank() && exerciseType.isNotBlank()
                        && selectedReps > 0 && selectedSets > 0
                    ) {
                        // Fetch the current user's ID
                        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                        val workout = Workout(
                            workoutId = "",  // Empty, as workoutId will be generated in the repository
                            exerciseType = exerciseType,
                            workoutName = workoutName,
                            sets = selectedSets,
                            reps = selectedReps,
                            userId = currentUserId?: "",  // Pass the actual userId here
                            dayId = selectedDayId
                        )

                        // Add workout and handle success
                        workoutViewModel.addWorkout(workout) { success ->
                            if (success) {
                                showDialog = true // Trigger success callback
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Failed to add workout. Please try again.")
                                }
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill all fields.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Workout")
            }
        }
    }
}

@Composable
fun DropdownMenuWithLabel(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            Text(
                text = if (selectedOption.isEmpty()) "Select $label" else selectedOption,
                style = MaterialTheme.typography.bodySmall
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    { Text(text = option) }, onClick = {
                        onOptionSelected(option)
                        expanded = false
                    })
            }
        }
    }
}