package com.example.ai_life.data.ml

import android.content.Context
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.coroutines.tasks.await
import java.io.File

object ModelDownloaderUtil {

    private const val MODEL_NAME = "diagnosis" // Debe coincidir con el nombre en Firebase Console

    /**
     * Descarga (o recupera de cache) el .tflite remoto y devuelve el File local.
     * Lanza excepción si falla.
     */
    suspend fun fetchModelFile(context: Context): File? {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi() // quita si quieres datos móviles
            .build()

        val modelFile = FirebaseModelDownloader.getInstance()
            .getModel(MODEL_NAME, DownloadType.LOCAL_MODEL, conditions)
            .await()

        return modelFile.file
    }
}
