package com.example.ai_life.presentation.util

object Constants {
    /** Lista de diagnósticos, en el mismo orden que entrenaste el LabelEncoder */
    val DIAGNOSIS_LABELS = listOf(
        "Normal",
        "Fiebre aislada",
        "Taquicardia sin fiebre",
        "Hipoxia",
        "Fiebre con taquicardia",
        "Fiebre con hipoxia",
        "Emergencia médica"
    )

    /** Mapa de recomendaciones por etiqueta */
    val DIAGNOSIS_RECOMMENDATIONS = mapOf(
        "Normal"                  to "Sigue tu rutina habitual, mantén hidratación.",
        "Fiebre aislada"          to "Descansa, toma antipiréticos si es necesario.",
        "Taquicardia sin fiebre"  to "Evita esfuerzos y controla tu pulso.",
        "Hipoxia"                 to "Consulta a tu médico y controla tu oxígeno.",
        "Fiebre con taquicardia"  to "Reposa, toma temperatura y consulta si empeora.",
        "Fiebre con hipoxia"      to "Busca atención médica urgente.",
        "Emergencia médica"       to "Acude de inmediato al servicio de urgencias."
    )
}