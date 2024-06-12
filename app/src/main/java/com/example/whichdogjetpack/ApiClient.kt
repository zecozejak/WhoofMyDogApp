package com.example.whichdogjetpack

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://dog-breed-identifier-v2.onrender.com"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Ustaw czas oczekiwania na połączenie
        .readTimeout(30, TimeUnit.SECONDS) // Ustaw czas oczekiwania na odpowiedź
        .writeTimeout(30, TimeUnit.SECONDS) // Ustaw czas oczekiwania na zapis
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
