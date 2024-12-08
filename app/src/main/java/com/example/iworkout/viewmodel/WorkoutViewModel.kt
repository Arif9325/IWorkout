package com.example.iworkout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iworkout.data.model.Workout
import com.example.iworkout.data.repository.UserRepository
import com.example.iworkout.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {

    private val workoutRepository = WorkoutRepository()
    private val userRepository = UserRepository()

    private val _currentUserId = MutableStateFlow<String?>(null)

    init {
        // Fetch the current user ID at initialization
        viewModelScope.launch {
            fetchCurrentUserId()
        }
    }

    private fun fetchCurrentUserId() {
        val username = "currentUsername"  // Replace with the actual username value
        val password = "currentPassword"  // Replace with the actual password value

        userRepository.getCurrentUserId(username, password) { userId ->
            _currentUserId.value = userId
        }
    }

    fun addWorkout(workout: Workout, onComplete: (Boolean) -> Unit) {
        workoutRepository.addWorkout(workout, onComplete)
    }

    fun getWorkoutsForDay(userId: Int, dayId: Int, onResult: (List<Workout>) -> Unit) {
        workoutRepository.getWorkoutsForDay(userId, dayId, onResult)
    }

    fun getCurrentUserIdAsInt(): Int? {
        return _currentUserId.value?.toIntOrNull()  // Convert userId from String to Int
    }
}
