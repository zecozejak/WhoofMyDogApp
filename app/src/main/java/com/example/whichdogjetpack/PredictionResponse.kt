package com.example.whichdogjetpack

data class PredictionResponse(
    val predicted_breed: String,
    val confidence: Double,
    val top_3: List<TopBreed>
)

data class TopBreed(
    val breed: String,
    val confidence: Double
)
