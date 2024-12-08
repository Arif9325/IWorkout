package com.example.iworkout.data.repository

import android.util.Log
import com.example.iworkout.data.model.Workout
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutRepository {

    private val db = FirebaseFirestore.getInstance()
    fun addWorkout(workout: Workout, onComplete: (Boolean) -> Unit) {
        // Add workout document to Firestore, Firestore will auto-generate a unique ID
        db.collection("workouts")
            .add(workout)
            .addOnSuccessListener { documentReference ->
                // Update the workoutId field with the generated document ID
                val workoutId = documentReference.id
                db.collection("workouts")
                    .document(workoutId)
                    .update("workoutId", workoutId)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Workout ID updated successfully")
                        onComplete(true)  // Callback indicating success
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore", "Error updating workoutId", exception)
                        onComplete(false)  // Callback indicating failure
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error adding workout", exception)
                onComplete(false)  // Callback indicating failure
            }
    }

    // Fetch workouts for a specific user and day
    fun getWorkoutsForDay(userId: String, dayId: String, onResult: (List<Workout>) -> Unit) {
        println("Fetching workouts for userId: $userId, dayId: $dayId")

        var temp = db.collection("workouts")

        temp.whereEqualTo("userId", userId)
            .whereEqualTo("dayId", dayId.toInt())
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val workouts = documents.map { it.toObject(Workout::class.java) }
                    println("Fetched workouts: $workouts")
                    onResult(workouts)
                } else {
                    println("No workouts found for userId: $userId, dayId: $dayId")
                    onResult(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                println("Error fetching workouts: ${exception.message}")
                onResult(emptyList())
            }
    }

}