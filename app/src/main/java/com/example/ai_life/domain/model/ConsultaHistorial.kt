package com.example.ai_life.domain.model

data class ConsultaHistorial(
    val code: String = "",
    val bpm: Int = 0,
    val spo2: Int = 0,
    val temperatura: Double = 0.0,
    val diagnosis: String = "",
    val timestamp: String = ""
)