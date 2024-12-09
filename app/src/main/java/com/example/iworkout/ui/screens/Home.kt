package com.example.iworkout.ui.screens

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.example.iworkout.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.iworkout.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    var userInput by remember { mutableStateOf("") }
    var aiResponse by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Home", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            HeaderSection(navController)
            Spacer(modifier = Modifier.height(16.dp))
            WorkoutGrid()
            Spacer(modifier = Modifier.height(32.dp))
            ActionButtons(navController) { showDialog = true }
        }

        // Chat with AI Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Chat with AI") },
                text = {
                    Column {
                        if (aiResponse.isNotEmpty()) {
                            Text("AI: $aiResponse", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        OutlinedTextField(
                            value = userInput,
                            onValueChange = { userInput = it },
                            label = { Text("Your message") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (userInput.isNotBlank()) {
                                callOpenAI(userInput) { response ->
                                    aiResponse = response
                                }
                                userInput = ""
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Please enter a message.")
                                }
                            }
                        }
                    ) {
                        Text("Send")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun HeaderSection(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var Name by remember { mutableStateOf<String?>(null) }
    authViewModel.fetchName(userId) { name ->
        Name = name
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hello $Name",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.weight(1f)
        )
        Button(onClick = { navController.navigate("login") }) {
            Text(text = "LOGOUT")
        }
    }
}

@Composable
fun WorkoutGrid() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WorkoutCard("Person JumpRoping", R.drawable.ropeskippinh)
            WorkoutCard("Person Squatting", R.drawable.personsquatting)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WorkoutCard("Person Meditating", R.drawable.meditation)
            WorkoutCard("Person doing pull ups", R.drawable.pullups)
        }
    }
}

@Composable
fun WorkoutCard(title: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(150.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ActionButtons(navController: NavHostController, onChatWithAI: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Button(
            onClick = { navController.navigate("view_workouts") },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "View Workouts")
        }
        Button(
            onClick = { navController.navigate("add_workout") },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Add Workout")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onChatWithAI() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Chat with AI")
        }
    }
}

 // Function to call OpenAI API
fun callOpenAI(message: String, onResponse: (String) -> Unit) {
    val client = OkHttpClient()
    val apiUrl = "https://api.openai.com/v1/chat/completions"
    val apiKey = "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"

    val jsonBody = JSONObject().apply {
        put("model", "gpt-4") // Specify the model
        put("messages", JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("content", message)
            })
        })
        put("max_tokens", 100)
    }

    val requestBody = RequestBody.create(
        "application/json; charset=utf-8".toMediaTypeOrNull(),
        jsonBody.toString()
    )

    val request = Request.Builder()
        .url(apiUrl)
        .addHeader("Authorization", "Bearer $apiKey")
        .addHeader("Content-Type", "application/json")
        .post(requestBody)
        .build()

    Log.d("API_URL", apiUrl)
    Log.d("Request_Headers", "Authorization: Bearer $apiKey, Content-Type: application/json")
    Log.d("Request_Body", jsonBody.toString())

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResponse("Failed to connect to AI service. Please try again. Error: ${e.message}")
        }


        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody ?: "")
                val aiText = jsonResponse.optJSONArray("choices")
                    ?.optJSONObject(0)
                    ?.optJSONObject("message")
                    ?.optString("content")
                    ?: "No response from AI."
                onResponse(aiText.trim())
            } else {
                val errorCode = response.code
                val errorMessage = response.message
                val errorBody = response.body?.string()

                val detailedError = """
                    Error Code: $errorCode
                    Message: $errorMessage
                    Details: ${errorBody ?: "No additional details provided."}
                """.trimIndent()

                onResponse(detailedError)
            }
        }
    })
}