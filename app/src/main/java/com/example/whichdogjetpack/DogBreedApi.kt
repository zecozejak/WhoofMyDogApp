package com.example.whichdogjetpack

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DogBreedApi {
    @Multipart
    @POST("/predict")
    fun uploadImage(@Part file: MultipartBody.Part, @Part("file") name: RequestBody): Call<PredictionResponse>
}
