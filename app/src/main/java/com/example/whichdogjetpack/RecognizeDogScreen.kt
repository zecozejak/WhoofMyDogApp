package com.example.whichdogjetpack

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RecognizeDogScreen(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var result by remember { mutableStateOf<String?>(null) }
    var photoFile by remember { mutableStateOf<File?>(null) }
    var prediction by remember { mutableStateOf<PredictionResponse?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            photoFile?.let {
                val uri = Uri.fromFile(it)
                bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                imageUri = uri
                Log.d("RecognizeDogScreen", "Photo taken: $uri")
            }
        } else {
            Log.d("RecognizeDogScreen", "Photo taking failed")
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            Log.d("RecognizeDogScreen", "Picked Image URI: $uri")
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (context.checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    photoFile = createImageFile(context)
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile!!)
                    imageUri = uri
                    takePictureLauncher.launch(uri)
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }) {
                Text("Zrób zdjęcie")
            }

            Button(onClick = { pickImageLauncher.launch("image/*") }) {
                Text("Wybierz zdjęcie")
            }

            Button(onClick = {
                imageUri?.let { uri ->
                    uploadImage(uri, context) { response, pred ->
                        result = response
                        prediction = pred
                    }
                }
            }) {
                Text("Wyślij zdjęcie")
            }

            Button(onClick = {
                imageUri = null
                bitmap = null
                result = null
                prediction = null
            }) {
                Text("Powtórz zdjęcie")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        prediction?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Twój pies to: ${it.predicted_breed}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    it.top_3.forEach { breed ->
                        Text(
                            text = "${breed.breed}: ${"%.2f".format(breed.confidence)}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}

fun uploadImage(uri: Uri, context: Context, onResult: (String, PredictionResponse?) -> Unit) {
    val file = getFileFromUri(uri, context)
    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
    val name = RequestBody.create("text/plain".toMediaTypeOrNull(), "file")

    val api = ApiClient.instance.create(DogBreedApi::class.java)
    val call = api.uploadImage(body, name)
    call.enqueue(object : Callback<PredictionResponse> {
        override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
            if (response.isSuccessful) {
                val prediction = response.body()
                prediction?.let {
                    val result = "Predicted Breed: ${it.predicted_breed}\nConfidence: ${it.confidence}"
                    onResult(result, it)
                }
            } else {
                onResult("Request failed. Response code: ${response.code()}", null)
            }
        }

        override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
            Log.e("RecognizeDogScreen", "Request failed", t)
            onResult("Request failed: ${t.message}", null)
        }
    })
}

fun getFileFromUri(uri: Uri, context: Context): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File(context.cacheDir, "temp_image.jpg")
    tempFile.outputStream().use { fileOut ->
        inputStream?.copyTo(fileOut)
    }
    return tempFile
}

