package com.example.iworkout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iworkout.data.model.Workout
import com.example.iworkout.data.repository.UserRepository
import com.example.iworkout.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow

class WorkoutViewModel : ViewModel() {

    private val workoutRepository = WorkoutRepository()

    private val _currentUserId = MutableStateFlow<String?>(null)

    fun addWorkout(workout: Workout, onComplete: (Boolean) -> Unit) {
        workoutRepository.addWorkout(workout, onComplete)
    }

    fun getWorkoutsForDay(userId: String, dayId: String, onResult: (List<Workout>) -> Unit) {
        workoutRepository.getWorkoutsForDay(userId, dayId, onResult)
    }

    fun getCurrentUserIdAsInt(): Int? {
        return _currentUserId.value?.toIntOrNull()  // Convert userId from String to Int
    }
}
