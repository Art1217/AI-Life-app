package com.example.ai_life.domain.model

data class Consulta(
    var code: String = "",          // si quieres guardar luego la clave como parte del objeto
    val bpm: Int = 0,
    val spo2: Int = 0,
    val temperatura: Double = 0.0
)