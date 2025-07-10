// app/src/main/java/com/example/ai_life/presentation/screens/viewmodel/DashboardViewModel.kt
package com.example.ai_life.presentation.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai_life.data.user.FirebaseUserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val userRepo: FirebaseUserRepository = FirebaseUserRepository()
) : ViewModel() {

    private val _nombreCompleto = MutableStateFlow("")
    val nombreCompleto: StateFlow<String> = _nombreCompleto

    private val _dni = MutableStateFlow("")
    val dni: StateFlow<String> = _dni

    private val _localidad = MutableStateFlow("")
    val localidad: StateFlow<String> = _localidad

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    init {
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
}
