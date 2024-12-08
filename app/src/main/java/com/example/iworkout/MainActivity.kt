package com.example.iworkout

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.iworkout.ui.navigation.NavGraph
import com.example.iworkout.ui.theme.IWorkoutTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = FirebaseFirestore.getInstance()
        db.collection("test")
            .document("testDoc")
            .set(mapOf("field" to "value"))
            .addOnSuccessListener {
                Log.d("FirestoreTest", "Test document added successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreTest", "Error adding test document", e)
            }
        enableEdgeToEdge()
        setContent {
            IWorkoutTheme {

                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    NavGraph(navController)
}

