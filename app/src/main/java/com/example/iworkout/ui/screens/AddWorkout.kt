package com.example.iworkout.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkout(
    onWorkoutSuccess: () -> Unit,
    workoutViewModel: WorkoutViewModel = viewModel(),
    navController: NavHostController
) {
    var selectedDayId by remember { mutableStateOf(0) }
    var workoutName by remember { mutableStateOf("") }
    var exerciseType by remember { mutableStateOf("") }
    var selectedReps by remember { mutableStateOf(0) }
    var selectedSets by remember { mutableStateOf(0) }

    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val daysOfWeek = listOf( "M", "T", "W", "T", "F", "S","S")
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
                    onWorkoutSuccess()
                }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Add Workout", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ){ paddingValues ->
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
                        onClick = { selectedDayId = index + 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedDayId == index + 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier
                            .weight(1f)

                    ) {
                        // Display day letter inside the button
                        Text(
                            text = day,
                            color = if (selectedDayId == index + 1) Color.White else Color.Black, // Change color based on selection
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            // Workout Name Input
            OutlinedTextField(
                value = workoutName,
                onValueChange = { workoutName = it },
                label = { Text("Workout Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary, // Border color when focused
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface, // Border color when not focused
                    focusedLabelColor = MaterialTheme.colorScheme.primary, // Label color when focused
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface // Label color when not focused
                )
            )

            // Exercise Type Input
            OutlinedTextField(
                value = exerciseType,
                onValueChange = { exerciseType = it },
                label = { Text("Exercise Type") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary, // Border color when focused
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface, // Border color when not focused
                    focusedLabelColor = MaterialTheme.colorScheme.primary, // Label color when focused
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface // Label color when not focused
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Reps and Sets Inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    DropdownMenuWithLabel(
                        label = "Reps",
                        options = repsOptions.map { it.toString() },
                        selectedOption = if (selectedReps == 0) "" else selectedReps.toString(),
                        onOptionSelected = { selectedReps = it.toInt() }
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    DropdownMenuWithLabel(
                        label = "Sets",
                        options = setsOptions.map { it.toString() },
                        selectedOption = if (selectedSets == 0) "" else selectedSets.toString(),
                        onOptionSelected = { selectedSets = it.toInt() }
                    )
                }
            }

            // Add Workout Button
            Button(
                onClick = {
                    if (selectedDayId != 0 && workoutName.isNotBlank() && exerciseType.isNotBlank()
                        && selectedReps > 0 && selectedSets > 0
                    ) {
                        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                        val workout = Workout(
                            workoutId = "",
                            exerciseType = exerciseType,
                            workoutName = workoutName,
                            sets = selectedSets,
                            reps = selectedReps,
                            userId = currentUserId ?: "",
                            dayId = selectedDayId
                        )

                        workoutViewModel.addWorkout(workout) { success ->
                            if (success) {
                                showDialog = true
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
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
                .background(MaterialTheme.colorScheme.secondaryContainer) // Hardcoded background color
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
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    text = { Text(text = option) }
                )
            }
        }
    }
}
