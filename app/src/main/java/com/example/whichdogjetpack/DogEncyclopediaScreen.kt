package com.example.whichdogjetpack

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DogEncyclopediaScreen(navController: NavController) {
    val context = LocalContext.current
    var dogs by remember { mutableStateOf<List<Dog>>(emptyList()) }
    var filteredDogs by remember { mutableStateOf<List<Dog>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            dogs = loadDogsFromCsv(context)
            filteredDogs = dogs
        } catch (e: Exception) {
            errorMessage = "Error loading data: ${e.message}"
        }
    }

// Mapowanie wartości na odpowiedniki
    val mappedFilteredDogs = filteredDogs.map { dog ->
        Dog(
            breed = dog.breed,
            fur = dog.fur,
            fur_color = dog.fur_color,
            image = dog.image,
            life_expectancy = dog.life_expectancy,
            description = dog.description
        )
    }

    // Filtruj unikalne psy
    val uniqueMappedFilteredDogs = mappedFilteredDogs.distinctBy { it.copy(image = "") }

    Scaffold(
        topBar = {
            Button(onClick = { navController.popBackStack() }) {
                Text("Powrót")
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { newText ->
                        searchText = newText
                        filteredDogs = dogs.filter { it.breed.contains(newText, ignoreCase = true) }
                    },
                    label = { Text("Wyszukaj rasę psa") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                if (errorMessage != null) {
                    Text(text = errorMessage!!)
                } else {
                    LazyColumn {
                        items(uniqueMappedFilteredDogs) { dog ->
                            DogItem(dog)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun DogItem(dog: Dog) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        if (dog.image.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(dog.image),
                contentDescription = dog.breed,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 16.dp)
            )
        }
        Column {
            Text(text = "Rasa: ${dog.breed}")
            Text(text = "Futro: ${dog.fur}")
            Text(text = "Kolor: ${dog.fur_color}")
            Text(text = "Długość życia: ${dog.life_expectancy}")
            Text(text = "Opis rasy: ${dog.description}")
        }
    }
}