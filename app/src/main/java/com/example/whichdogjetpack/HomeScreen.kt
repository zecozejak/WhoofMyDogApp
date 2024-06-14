package com.example.whichdogjetpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Dodanie logo
        Image(
            painter = painterResource(id = R.drawable.logo), // zamień na rzeczywiste id zasobu logo
            contentDescription = "Logo",
            modifier = Modifier
                .height(400.dp)
                .width(400.dp)
                .padding(bottom = 32.dp),
            contentScale = ContentScale.Fit
        )

        Button(onClick = { navController.navigate("encyclopedia") }) {
            Text("Przejdź do encyklopedii psów")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("recognize") }) {
            Text("Rozpoznaj rasę psa")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("mydogs") }) {
            Text("Moje Pieski")
        }
    }
}
