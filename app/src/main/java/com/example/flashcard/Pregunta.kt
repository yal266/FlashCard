package com.example.flashcard

import java.io.Serializable

data class Pregunta(
    val texto: String,
    val opciones: List<String>,
    val opcionCorrecta: Int
) : Serializable
