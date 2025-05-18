package com.example.flashcard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var contenedor: LinearLayout
    private lateinit var preguntas: List<Pregunta>
    private var preguntaActual = 0

    private var respuestasCorrectas = 0
    private var respuestasIncorrectas = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Flashcards"

        contenedor = findViewById(R.id.contenedorPreguntas)

        try {
            preguntas = cargarPreguntasDesdeJson()

            if (preguntas.isEmpty()) {
                mostrarMensajeError("No se encontraron preguntas en el archivo JSON")
                return
            }
            mostrarPregunta(preguntaActual)

        } catch (e: Exception) {
            when (e) {
                is IOException -> mostrarMensajeError("Error al leer el archivo de preguntas")
                is JsonSyntaxException -> mostrarMensajeError("El formato del archivo JSON es incorrecto")
                else -> mostrarMensajeError("Error inesperado: ${e.message}")
            }
        }
    }

    private fun cargarPreguntasDesdeJson(): List<Pregunta> {
        val jsonStr = assets.open("questions.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(jsonStr, Array<Pregunta>::class.java).toList()
    }

    private fun mostrarPregunta(index: Int) {
        contenedor.removeAllViews()

        if (index < 0 || index >= preguntas.size) {
            mostrarMensajeError("Índice de pregunta fuera de rango")
            return
        }

        val pregunta = preguntas[index]

        val txtProgreso = TextView(this).apply {
            text = "Pregunta ${index + 1} de ${preguntas.size}"
            textSize = 14f
            setPadding(16, 16, 16, 32)
            setTextColor(Color.GRAY)
        }
        contenedor.addView(txtProgreso)

        val txtPregunta = TextView(this).apply {
            text = pregunta.texto
            textSize = 20f
            setPadding(16, 16, 16, 32)
        }
        contenedor.addView(txtPregunta)

        for ((i, opcion) in pregunta.opciones.withIndex()) {
            val txtOpcion = TextView(this).apply {
                text = opcion
                textSize = 16f
                setPadding(24, 16, 24, 16)
                setBackgroundColor(Color.parseColor("#EEEEEE"))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 10, 8, 8)
                layoutParams = params

                setOnClickListener {
                    val esCorrecta = (i == pregunta.opcionCorrecta)

                    if (esCorrecta) {
                        respuestasCorrectas++
                    } else {
                        respuestasIncorrectas++
                    }
                    setBackgroundColor(
                        if (esCorrecta) Color.parseColor("#AAFFAA")
                        else Color.parseColor("#FFAAAA")
                    )

                    val mensaje = if (esCorrecta) "¡Correcto!" else
                        "Incorrecto. La respuesta correcta era: ${pregunta.opciones[pregunta.opcionCorrecta]}"
                    Toast.makeText(this@MainActivity, mensaje, Toast.LENGTH_SHORT).show()

                    deshabilitarOpciones()

                    android.os.Handler().postDelayed({
                        mostrarBotonSiguiente()
                    }, 3500)
                }
            }
            contenedor.addView(txtOpcion)
        }
    }

    private fun deshabilitarOpciones() {
        for (i in 0 until contenedor.childCount) {
            val view = contenedor.getChildAt(i)
            if (view is TextView && view.background != null) {
                view.isClickable = false
            }
        }
    }

    private fun mostrarBotonSiguiente() {
        if (preguntaActual < preguntas.size - 1) {
            val btnSiguiente = TextView(this).apply {
                text = "SIGUIENTE PREGUNTA"
                textSize = 16f
                setPadding(24, 16, 24, 16)
                setBackgroundColor(Color.parseColor("#4285F4"))
                setTextColor(Color.WHITE)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 32, 8, 8)
                layoutParams = params

                setOnClickListener {
                    preguntaActual++
                    mostrarPregunta(preguntaActual)
                }
            }
            contenedor.addView(btnSiguiente)
        } else {
            mostrarResumenResultados()
        }
    }

    private fun mostrarResumenResultados() {
        contenedor.removeAllViews()

        val totalPreguntas = respuestasCorrectas + respuestasIncorrectas
        val porcentajeCorrectas = if (totalPreguntas > 0) {
            (respuestasCorrectas.toFloat() / totalPreguntas.toFloat() * 100).toInt()
        } else {
            0
        }

        val txtTituloResumen = TextView(this).apply {
            text = "Resumen de Resultados"
            textSize = 22f
            setPadding(16, 32, 16, 32)
            gravity = android.view.Gravity.CENTER
            setTextColor(Color.parseColor("#333333"))
        }
        contenedor.addView(txtTituloResumen)

        val txtCorrectas = TextView(this).apply {
            text = "Respuestas correctas: $respuestasCorrectas"
            textSize = 18f
            setPadding(24, 16, 24, 16)
            setTextColor(Color.parseColor("#4CAF50"))
        }
        contenedor.addView(txtCorrectas)

        val txtIncorrectas = TextView(this).apply {
            text = "Respuestas incorrectas: $respuestasIncorrectas"
            textSize = 18f
            setPadding(24, 16, 24, 16)
            setTextColor(Color.parseColor("#F44336"))
        }
        contenedor.addView(txtIncorrectas)

        val txtTotal = TextView(this).apply {
            text = "Total de preguntas: $totalPreguntas"
            textSize = 18f
            setPadding(24, 16, 24, 16)
            setTextColor(Color.parseColor("#333333"))
        }
        contenedor.addView(txtTotal)

        val txtPorcentaje = TextView(this).apply {
            text = "Porcentaje de aciertos: $porcentajeCorrectas%"
            textSize = 20f
            setPadding(24, 32, 24, 32)
            setTextColor(Color.parseColor("#2196F3"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        contenedor.addView(txtPorcentaje)

        val mensajeCalificacion = when {
            porcentajeCorrectas >= 90 -> "¡Excelente trabajo!"
            porcentajeCorrectas >= 75 -> "¡Muy buen trabajo!"
            porcentajeCorrectas >= 60 -> "¡Buen trabajo!"
            porcentajeCorrectas >= 40 -> "Necesitas practicar más."
            else -> "Deberías repasar el material nuevamente."
        }

        val txtMensaje = TextView(this).apply {
            text = mensajeCalificacion
            textSize = 18f
            setPadding(24, 16, 24, 48)
            gravity = android.view.Gravity.CENTER
            setTextColor(Color.parseColor("#333333"))
        }
        contenedor.addView(txtMensaje)

        val btnReiniciar = TextView(this).apply {
            text = "REINICIAR"
            textSize = 16f
            setPadding(24, 16, 24, 16)
            setBackgroundColor(Color.parseColor("#4285F4"))
            setTextColor(Color.WHITE)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 16, 8, 8)
            layoutParams = params

            setOnClickListener {
                preguntaActual = 0
                respuestasCorrectas = 0
                respuestasIncorrectas = 0
                mostrarPregunta(preguntaActual)
            }
        }
        contenedor.addView(btnReiniciar)
    }

    private fun mostrarMensajeError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

        val txtError = TextView(this).apply {
            text = mensaje
            textSize = 18f
            setPadding(32, 32, 32, 32)
            setTextColor(Color.RED)
        }
        contenedor.addView(txtError)
    }
}