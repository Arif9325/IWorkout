package com.example.iworkout.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.iworkout.data.model.User
import com.example.iworkout.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val userRepository = UserRepository()

    fun fetchName(userId: String?, onResult: (String?) -> Unit) {
        userRepository.fetchName(userId, onResult)
    }

    fun signUp(user: User, password: String, onComplete: (Boolean, String?) -> Unit) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.username + "@example.com", password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user
                    user.userId = firebaseUser?.uid ?: "" // Save the Firebase UID
                    userRepository.addUser(user) { success ->
                        onComplete(success, null)
                    }
                } else {
                    val exception = task.exception
                    val errorMessage = if (exception?.message?.contains("The email address is already in use") == true) {
                        "User already exists."
                    } else {
                        "Sign up failed. Try again."
                    }
                    onComplete(false, errorMessage)
                }
            }
    }

    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        val email = username + "@example.com"  // Convert username to email format
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }
}