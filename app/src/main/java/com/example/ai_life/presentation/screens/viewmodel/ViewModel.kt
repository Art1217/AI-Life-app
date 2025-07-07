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

    private val db = Firebase.database.reference.child("consultas")

    // 1) Código ingresado por el usuario
    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code

    // 2) Resultado de la búsqueda
    private val _consultas = MutableStateFlow<List<Consulta>>(emptyList())
    val consultas: StateFlow<List<Consulta>> = _consultas

    // 3) Mensaje de estado (por ejemplo para errores o “no encontrado”)
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

        viewModelScope.launch {
            // limpiamos estado anterior
            _status.value = "Buscando..."
            _consultas.value = emptyList()

            db.child(lookup).get()
                .addOnSuccessListener { snap ->
                    val consulta = snap.getValue(Consulta::class.java)
                    if (consulta != null) {
                        // único resultado; si tu estructura almacena listas,
                        // ajusta aquí para mapear children
                        _consultas.value = listOf(consulta)
                        _status.value = null
                    } else {
                        _status.value = "No se encontró la consulta"
                    }
                }
                .addOnFailureListener { e ->
                    _status.value = "Error: ${e.localizedMessage}"
                }
        }
    }
}