package com.example.iworkout.data.model

data class Workout(
    val workoutId: String = "",
    val exerciseType: String = "",
    val workoutName: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val userId: String = "",
    val dayId: Int = 0
)
