// app/src/main/java/com/example/ai_life/data/ml/ModelDownloaderUtil.kt
package com.example.ai_life.data.ml

import android.content.Context
import android.util.Log
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.coroutines.tasks.await
import java.io.File

object ModelDownloaderUtil {
    private const val TAG = "ModelDownloaderUtil"
    private const val MODEL_NAME = "diagnosis"

    /**
     * Descarga (o recupera de cache) el .tflite remoto y devuelve el File local.
     * Si forceRefresh=true, elimina antes la versión cacheada.
     */
    suspend fun fetchModelFile(
        context: Context,
        forceRefresh: Boolean = false
    ): File {
        val downloader = FirebaseModelDownloader.getInstance()

        if (forceRefresh) {
            Log.d(TAG, "Borrando modelo cacheado: $MODEL_NAME")
            downloader.deleteDownloadedModel(MODEL_NAME)
        }

        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        Log.d(TAG, "Solicitando descarga de $MODEL_NAME (LATEST_MODEL)…")
        val modelFileInfo = downloader
            .getModel(MODEL_NAME, DownloadType.LATEST_MODEL, conditions)
            .await()

        val file = modelFileInfo.file
        Log.d(TAG, "Ruta recibida: ${file?.absolutePath}")

        require(file != null && file.exists()) { "Modelo descargado pero no válido" }
        val len = file.length()
        Log.d(TAG, "Tamaño del archivo: $len bytes")
        require(len > 0L) { "Modelo descargado con 0 bytes, archivo inválido" }

        return file
    }
}
