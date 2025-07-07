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
        if (lookup.isEmpty()) { /* … */ }

        _status.value    = "Buscando..."
        _consultas.value = emptyList()

        db.child(lookup)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snap = task.result
                    when {
                        !snap.exists() -> _status.value = "No existe código \"$lookup\""
                        else -> {
                            try {
                                val consulta = snap.getValue(Consulta::class.java)
                                if (consulta != null) {
                                    // inyectamos la clave en la propiedad code
                                    consulta.code = snap.key ?: ""
                                    _consultas.value = listOf(consulta)
                                    _status.value    = null
                                } else {
                                    _status.value = "Los datos no coinciden con el modelo Consulta"
                                }
                            } catch (e: Exception) {
                                _status.value = "Error al deserializar:\n${e.message}"
                            }
                        }
                    }
                } else {
                    _status.value = "Error Firebase:\n${task.exception?.message}"
                }
            }
            .addOnFailureListener {
                _status.value = "Fallo red/permiso:\n${it.message}"
            }
    }
}
