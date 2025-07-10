package com.example.ai_life.presentation.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_life.domain.model.Consulta
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConsultaViewModel : ViewModel() {

    // ⚠️ Si tus números están directamente bajo el root de la RTDB,
    //    usa Firebase.database.reference.
    //    Si están dentro de /consultas, haz Firebase.database.reference.child("consultas")
    private val db = Firebase.database.reference

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code

    private val _consultas = MutableStateFlow<List<Consulta>>(emptyList())
    val consultas: StateFlow<List<Consulta>> = _consultas

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    fun onCodeChange(new: String) {
        _code.value = new
    }

    fun searchConsulta() {
        val lookup = code.value.trim()
        if (lookup.isEmpty()) {
            _status.value = "Ingresa un código"
            _consultas.value = emptyList()
            return
        }

        _status.value = "Buscando..."
        _consultas.value = emptyList()

        db.child(lookup).get()
            .addOnSuccessListener { snap ->
                if (snap.exists()) {
                    val bpm = snap.child("bpm").getValue(Int::class.java)
                    val spo2 = snap.child("spo2").getValue(Int::class.java)
                    val temp = snap.child("temperatura").getValue(Double::class.java)
                    if (bpm != null && spo2 != null && temp != null) {
                        val consulta = Consulta(
                            code = lookup,
                            bpm = bpm,
                            spo2 = spo2,
                            temperatura = temp
                        )
                        _consultas.value = listOf(consulta)
                        _status.value = "Consulta encontrada"
                    } else {
                        _status.value = "Datos incompletos para el código $lookup"
                    }
                } else {
                    _status.value = "No se encontró el código $lookup"
                }
            }
            .addOnFailureListener { e ->
                _status.value = "Error al buscar: ${e.message}"
            }
    }
}