package com.example.flashcard

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PreguntaActivity : AppCompatActivity() {

    private lateinit var optionViews: ArrayList<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Using the existing layout

        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pregunta"

        val pregunta = intent.getSerializableExtra("pregunta") as? Pregunta
            ?: run {
                Toast.makeText(this, "Error: No se pudo cargar la pregunta", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        val contenedor = findViewById<LinearLayout>(R.id.contenedorPreguntas)
        contenedor.removeAllViews() // Clear any existing views

        // Add the question text
        val txtPregunta = TextView(this).apply {
            text = pregunta.texto
            textSize = 20f
            setPadding(16, 32, 16, 48)
        }
        contenedor.addView(txtPregunta)

        // Create option views
        optionViews = ArrayList()

        for ((index, opcion) in pregunta.opciones.withIndex()) {
            val txtOpcion = TextView(this).apply {
                text = opcion
                textSize = 16f
                setPadding(24, 16, 24, 16)
                setBackgroundColor(Color.parseColor("#EEEEEE"))
                setOnClickListener {
                    // Handle answer selection
                    handleOptionSelected(index, pregunta.opcionCorrecta)
                }
            }
            optionViews.add(txtOpcion)
            contenedor.addView(txtOpcion)
        }
    }

    private fun handleOptionSelected(selectedIndex: Int, correctIndex: Int) {
        // Disable all options to prevent multiple selections
        for (option in optionViews) {
            option.isClickable = false
        }

        // Highlight the selected option
        optionViews[selectedIndex].setBackgroundColor(
            if (selectedIndex == correctIndex) Color.parseColor("#AAFFAA") // Green for correct
            else Color.parseColor("#FFAAAA") // Red for incorrect
        )

        // Always highlight the correct answer
        optionViews[correctIndex].setBackgroundColor(Color.parseColor("#AAFFAA"))

        // Show result message
        val message = if (selectedIndex == correctIndex) {
            "¡Correcto!"
        } else {
            "Incorrecto. La respuesta correcta era: ${optionViews[correctIndex].text}"
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Handle back button in the action bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}