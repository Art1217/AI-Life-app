// app/src/main/java/com/example/ai_life/presentation/screens/viewmodel/DiagnosticoViewModel.kt
package com.example.ai_life.presentation.screens.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_life.data.ml.InterpreterUtil
import com.example.ai_life.data.ml.ModelDownloaderUtil
import com.example.ai_life.data.user.FirebaseUserRepository
import com.example.ai_life.domain.model.Consulta
import com.example.ai_life.domain.model.User
import com.example.ai_life.presentation.util.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.util.Calendar

class DiagnosticoViewModel(
    private val userRepo: FirebaseUserRepository = FirebaseUserRepository()
) : ViewModel() {

    companion object {
        private const val TAG = "DiagnosticoVM"
    }

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    private val _resultado = MutableStateFlow<Int?>(null)
    val resultado: StateFlow<Int?> = _resultado

    private val _diagnosticoEtiqueta = MutableStateFlow<String?>(null)
    val diagnosticoEtiqueta: StateFlow<String?> = _diagnosticoEtiqueta

    // Guardamos el interpreter para reutilizarlo
    private var interpreter: Interpreter? = null

    fun ejecutarDiagnostico(context: Context, consulta: Consulta) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "=== Iniciando diagnóstico: $consulta")

                // 1) Perfil y edad
                _status.value = "Cargando perfil..."
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("Usuario no autenticado")
                val user = userRepo.getUser(uid).getOrThrow()
                    ?: throw Exception("Perfil no encontrado")
                val edad = calcularEdad(user)
                Log.d(TAG, "Edad calculada: $edad")
                _status.value = "Edad: $edad"

                // 2) Sólo la primera vez descargamos y creamos el intérprete
                if (interpreter == null) {
                    _status.value = "Descargando modelo..."
                    val modelFile = ModelDownloaderUtil
                        .fetchModelFile(context, forceRefresh = false)
                        ?: throw Exception("Modelo no disponible")
                    Log.d(TAG, "Modelo descargado en: ${modelFile.absolutePath}")

                    _status.value = "Creando intérprete..."
                    interpreter = InterpreterUtil.createInterpreter(modelFile)
                    Log.d(TAG, "Interpreter creado")
                }

                // 3) Ejecutar inferencia
                val interp = interpreter!!
                val input = floatArrayOf(
                    edad.toFloat(),
                    consulta.temperatura.toFloat(),
                    consulta.bpm.toFloat(),
                    consulta.spo2.toFloat()
                )
                Log.d(TAG, "Input: ${input.joinToString()}")
                _status.value = "Ejecutando inferencia..."
                val resultIndex = InterpreterUtil.runInference(interp, input)
                Log.d(TAG, "Índice inferencia: $resultIndex")
                _resultado.value = resultIndex

                // 4) Mapear índice → etiqueta
                val etiqueta = Constants.DIAGNOSIS_LABELS
                    .getOrNull(resultIndex) ?: "Desconocido"
                _diagnosticoEtiqueta.value = etiqueta
                Log.d(TAG, "Etiqueta mapeada: $etiqueta")

                // 5) Resultado final
                _status.value = "Diagnóstico: $etiqueta"
                Log.d(TAG, "Diagnóstico completado: $etiqueta")

            } catch (e: Exception) {
                Log.e(TAG, "Error en diagnóstico", e)
                _status.value = "Error: ${e.message}"
            }
        }
    }

    private fun calcularEdad(user: User): Int {
        return try {
            val (d, m, y) = user.fechaNacimiento.split("/").map { it.toInt() }
            val hoy = Calendar.getInstance()
            val nac = Calendar.getInstance().apply { set(y, m - 1, d) }
            var edad = hoy.get(Calendar.YEAR) - nac.get(Calendar.YEAR)
            if (hoy.get(Calendar.DAY_OF_YEAR) < nac.get(Calendar.DAY_OF_YEAR)) edad--
            edad
        } catch (e: Exception) {
            Log.e(TAG, "Error calculando edad", e)
            0
        }
    }
}
