package com.example.ai_life.domain.model

data class Consulta(
    val code: String = "",
    val fecha: String = "",
    val resultados: String = "" // ajusta según tu estructura en RTDB
)