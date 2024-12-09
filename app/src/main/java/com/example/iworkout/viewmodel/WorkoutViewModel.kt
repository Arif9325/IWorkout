package com.example.iworkout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iworkout.data.model.Workout
import com.example.iworkout.data.repository.UserRepository
import com.example.iworkout.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow

class WorkoutViewModel : ViewModel() {

    private val workoutRepository = WorkoutRepository()

    fun addWorkout(workout: Workout, onComplete: (Boolean) -> Unit) {
        workoutRepository.addWorkout(workout, onComplete)
    }

    fun getWorkoutsForDay(userId: String, dayId: String, onResult: (List<Workout>) -> Unit) {
        workoutRepository.getWorkoutsForDay(userId, dayId, onResult)
    }

    fun getWorkoutById(workoutId: String, onResult: (Workout?) -> Unit) {
        workoutRepository.getWorkoutById(workoutId) { workout ->
            onResult(workout)
        }
    }

    // Function to update a workout
    fun updateWorkout(workout: Workout, onComplete: (Boolean) -> Unit) {
        workoutRepository.updateWorkout(workout) { success ->
            onComplete(success)
        }
    }

    fun deleteWorkout(workout: Workout, onComplete: (Boolean) -> Unit) {
        workoutRepository.deleteWorkout(workout, onComplete)
    }
}
