package com.example.whichdogjetpack

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

fun loadDogsFromCsv(context: Context, fileName: String = "dogs.csv"): List<Dog> {
    val dogs = mutableListOf<Dog>()
    try {
        val inputStream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.readLine() // Skip header
        reader.forEachLine {
            val tokens = it.split(",")
            if (tokens.size >= 7) { // ensure all required columns are present
                val dog = Dog(
                    breed = tokens[1],
                    fur = tokens[2],
                    fur_color = tokens[3],
                    image = tokens[4],
                    life_expectancy = tokens[5],
                    description = tokens[6]
                )
                dogs.add(dog)
            }
        }
    } catch (e: Exception) {
        Log.e("DataLoader", "Error reading CSV", e)
    }
    return dogs
}
