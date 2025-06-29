package com.example.ai_life.domain.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var uid: String = "",
    var nombres: String = "",
    var apellidos: String = "",
    var dni: String = "",
    var localidad: String = "",
    var fechaNacimiento: String = "",
    var sexo: String = "",
    var email: String = ""
) {
    // Firebase necesita un constructor vacío; el data class con valores por defecto ya lo proporciona.
}