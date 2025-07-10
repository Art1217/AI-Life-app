// app/src/main/java/com/example/ai_life/presentation/screens/viewmodel/ConsultaViewModel.kt
package com.example.ai_life.presentation.screens.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ai_life.domain.model.Consulta
import com.example.ai_life.domain.model.ConsultaHistorial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConsultaViewModel : ViewModel() {

    // Referencia al RTDB root
    private val db = Firebase.database.reference

    // --- Estado para búsqueda puntual ---
    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code

    private val _consultas = MutableStateFlow<List<Consulta>>(emptyList())
    val consultas: StateFlow<List<Consulta>> = _consultas

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    // --- Estado para historial en tiempo real ---
    private val _historial = MutableStateFlow<List<ConsultaHistorial>>(emptyList())
    val historial: StateFlow<List<ConsultaHistorial>> = _historial

    // Construimos la referencia al historial del usuario
    private val currentUid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val histRef = currentUid
        ?.let { Firebase.database.reference.child("users").child(it).child("consultas_historial") }

    init {
        // Si el usuario está autenticado, escuchamos en tiempo real los cambios
        histRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull {
                    it.getValue(ConsultaHistorial::class.java)
                }
                // Orden descendente por timestamp
                _historial.value = list.sortedByDescending { it.timestamp }
            }

            override fun onCancelled(error: DatabaseError) {
                // opcional: podrías actualizar un estado de error aquí
            }
        })
    }

    /** Actualiza el código ingresado */
    fun onCodeChange(new: String) {
        _code.value = new
    }

    /** Busca un único nodo root/{code} y expone el resultado en _consultas */
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
                    val bpm  = snap.child("bpm").getValue(Int::class.java)
                    val spo2 = snap.child("spo2").getValue(Int::class.java)
                    val temp = snap.child("temperatura").getValue(Double::class.java)
                    if (bpm != null && spo2 != null && temp != null) {
                        _consultas.value = listOf(
                            Consulta(
                                code       = lookup,
                                bpm        = bpm,
                                spo2       = spo2,
                                temperatura= temp
                            )
                        )
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

    /** Limpia la lista de búsqueda para no re-diagnosticar en recomposiciones */
    fun clearConsultas() {
        _consultas.value = emptyList()
    }
}
