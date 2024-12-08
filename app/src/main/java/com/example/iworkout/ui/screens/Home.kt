package com.example.iworkout.ui.screens

import android.os.Bundle
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

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.iworkout.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.iworkout.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavHostController) {
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
            Spacer(modifier = Modifier.height(16.dp))
            ActionButtons(navController)
        }
    }
}

@Composable
fun HeaderSection(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var Name by remember { mutableStateOf<String?>(null) }
    authViewModel.fetchName(userId) { name ->
        Name = name}


    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hello $Name",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.weight(1f)
        )
        Button(onClick = { navController.navigate("login")  }) {
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
            WorkoutCard("Person Meditating",R.drawable.meditation)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Use CardDefaults here
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
fun ActionButtons(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Add Workout button placed at the top
        Button(
            onClick = { navController.navigate("add_workout") },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
        ) {
            Text(text = "Add Workout")
        }

        // Chat with AI button placed at the bottom right
        Button(
            onClick = { /* to be implemented */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
        ) {
            Text(text = "Chat with AI")
        }
    }
}

