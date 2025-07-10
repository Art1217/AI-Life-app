// app/src/main/java/com/example/ai_life/presentation/screens/viewmodel/DashboardViewModel.kt
package com.example.ai_life.presentation.screens.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_life.data.user.FirebaseUserRepository
import com.example.ai_life.domain.model.ConsultaHistorial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel(
    private val userRepo: FirebaseUserRepository = FirebaseUserRepository()
) : ViewModel() {

    // Perfil del usuario
    private val _nombreCompleto = MutableStateFlow("")
    val nombreCompleto: StateFlow<String> = _nombreCompleto

    private val _dni = MutableStateFlow("")
    val dni: StateFlow<String> = _dni

    private val _localidad = MutableStateFlow("")
    val localidad: StateFlow<String> = _localidad

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    // Estadísticas globales
    private val _enfermedadMasComun = MutableStateFlow("")
    val enfermedadMasComun: StateFlow<String> = _enfermedadMasComun

    private val _sexoMasPropenso = MutableStateFlow("")
    val sexoMasPropenso: StateFlow<String> = _sexoMasPropenso

    private val _edadMin = MutableStateFlow(0)
    val edadMin: StateFlow<Int> = _edadMin

    private val _edadMax = MutableStateFlow(0)
    val edadMax: StateFlow<Int> = _edadMax

    // Formatos de fecha
    private val dateFormatHist = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateFormatBirth = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Referencia a todos los usuarios en RTDB
    private val usersRef: DatabaseReference =
        Firebase.database.reference.child("users")

    init {
        loadUserProfile()
        computeGlobalStats()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _status.value = "Cargando perfil..."
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid.isNullOrBlank()) {
                _status.value = "Usuario no autenticado"
                return@launch
            }
            userRepo.getUser(uid)
                .onSuccess { user ->
                    if (user != null) {
                        _nombreCompleto.value = "${user.nombres} ${user.apellidos}"
                        _dni.value = user.dni
                        _localidad.value = user.localidad
                        _status.value = null
                    } else {
                        _status.value = "Perfil no encontrado"
                    }
                }
                .onFailure { e ->
                    _status.value = "Error cargando perfil: ${e.message}"
                }
        }
    }

    private fun computeGlobalStats() {
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 1) Frecuencia de diagnósticos
                val freqDiag = mutableMapOf<String, Int>()
                snapshot.children.forEach { userSnap ->
                    userSnap.child("consultas_historial").children.forEach { histSnap ->
                        histSnap.getValue(ConsultaHistorial::class.java)
                            ?.let { registro ->
                                freqDiag[registro.diagnosis] =
                                    freqDiag.getOrDefault(registro.diagnosis, 0) + 1
                            }
                    }
                }
                val topDiag = freqDiag.maxByOrNull { it.value }?.key ?: ""
                _enfermedadMasComun.value = topDiag

                // 2) Sexo y rango de edad para esa enfermedad
                val sexCount = mutableMapOf<String, Int>()
                var minAge = Int.MAX_VALUE
                var maxAge = 0

                snapshot.children.forEach { userSnap ->
                    val sexo = userSnap.child("sexo").getValue(String::class.java) ?: return@forEach
                    val birthStr = userSnap.child("fechaNacimiento")
                        .getValue(String::class.java) ?: return@forEach

                    userSnap.child("consultas_historial").children.forEach { histSnap ->
                        histSnap.getValue(ConsultaHistorial::class.java)
                            ?.takeIf { it.diagnosis == topDiag }
                            ?.let { registro ->
                                // contar sexo
                                sexCount[sexo] = sexCount.getOrDefault(sexo, 0) + 1
                                // calcular edad
                                registro.timestamp
                                    .takeIf(String::isNotBlank)
                                    ?.let { tsStr ->
                                        val age = calculateAge(birthStr, tsStr)
                                        if (age in 0..120) {
                                            minAge = minOf(minAge, age)
                                            maxAge = maxOf(maxAge, age)
                                        }
                                    }
                            }
                    }
                }

                _sexoMasPropenso.value = sexCount.maxByOrNull { it.value }?.key ?: ""
                _edadMin.value = if (minAge == Int.MAX_VALUE) 0 else minAge
                _edadMax.value = maxAge
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DashboardVM", "Error calculando estadísticas", error.toException())
            }
        })
    }

    private fun calculateAge(birth: String, timestamp: String): Int {
        return try {
            val birthDate = dateFormatBirth.parse(birth)!!
            val histDate = dateFormatHist.parse(timestamp)!!
            val calBirth = Calendar.getInstance().apply { time = birthDate }
            val calHist = Calendar.getInstance().apply { time = histDate }
            var age = calHist.get(Calendar.YEAR) - calBirth.get(Calendar.YEAR)
            if (calHist.get(Calendar.DAY_OF_YEAR) < calBirth.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age
        } catch (e: Exception) {
            0
        }
    }
}
