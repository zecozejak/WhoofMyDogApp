package com.example.whichdogjetpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
        // Dodanie logo w ramce z zaokrąglonymi rogami
        Box(
            modifier = Modifier
                .size(400.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(4.dp, Color.Black, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { navController.navigate("encyclopedia") }) {
            Text("Przejdź do encyklopedii psów")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("recognize") }) {
            Text("Rozpoznaj rasę psa")
        }
    }
}

