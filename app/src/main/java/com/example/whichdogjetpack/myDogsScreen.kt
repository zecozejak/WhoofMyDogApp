package com.example.whichdogjetpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore

data class SavedDog(
    val breed: String = "",
    val imageUrl: String = ""
)

@Composable
fun MyDogsScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var dogs by remember { mutableStateOf(listOf<SavedDog>()) }

    LaunchedEffect(Unit) {
        db.collection("dogs")
            .get()
            .addOnSuccessListener { result ->
                dogs = result.map { document ->
                    SavedDog(
                        breed = document.getString("breed") ?: "",
                        imageUrl = document.getString("imageUrl") ?: ""
                    )
                }
            }
            .addOnFailureListener { exception ->
                // Obsługa błędów
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Moje Pieski",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp), // Przestrzeń na przycisk "Powrót"
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dogs.size) { index ->
                    val dog = dogs[index]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = rememberImagePainter(data = dog.imageUrl),
                            contentDescription = dog.breed,
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                        Text(dog.breed, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Powrót")
        }
    }
}
