package com.example.ai_life

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ai_life.presentation.navigation.NavGraph
import com.example.ai_life.ui.theme.Ai_LifeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        // --- Test Realtime Database ---
        val dbRef = Firebase.database.reference.child("connectionTest")
        dbRef.setValue("Test Connection")
            .addOnSuccessListener { Log.d("FireTest", "Escritura OK") }
            .addOnFailureListener { e -> Log.e("FireTest", "Error de escritura", e) }

        dbRef.get()
            .addOnSuccessListener { snap ->
                val v = snap.getValue(String::class.java)
                Log.d("FireTest", "Lectura OK: $v")
            }
            .addOnFailureListener { e ->
                Log.e("FireTest", "Error de lectura", e)
            }

        // --- Test de descarga del modelo TFLite desde Firebase ML ---
        // Configura condiciones de descarga (por Wi-Fi)
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        FirebaseModelDownloader.getInstance()
            .getModel(
                "diagnosis",                     // coincide con el nombre que pusiste en Firebase Console
                DownloadType.LOCAL_MODEL,        // baja el .tflite al dispositivo
                conditions
            )
            .addOnSuccessListener { modelFile ->
                // modelFile.file es el .tflite descargado
                val path = modelFile.file?.absolutePath
                Log.d("ModelTest", "Modelo descargado en: $path")
                Toast.makeText(this, "Modelo descargado OK", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Log.e("ModelTest", "Error descargando modelo", e)
                Toast.makeText(this, "Error descarga modelo: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }

        setContent {
            Ai_LifeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}
