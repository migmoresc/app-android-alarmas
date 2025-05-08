package com.migmoresc.alarmas

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

//https://www.flaticon.com/free-icon/bell_1827314?term=alarm&related_id=1827314

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RVAlarmasAdapter
    private val dataList = mutableListOf<String>()
    private lateinit var addElementButton: ImageButton
    private lateinit var etHora: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etHora = findViewById(R.id.etHora)

        val filter = object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val newText = dest?.subSequence(0, dstart).toString() +
                        (source?.subSequence(start, end)?.toString() ?: "") +
                        dest?.subSequence(dend, dest.length).toString()

                // Permitir borrar
                if (newText.isEmpty()) {
                    return null
                }

                try {
                    val input = newText.toInt()
                    if (input in 0..59) {
                        return null // Aceptar la entrada
                    }
                } catch (e: NumberFormatException) {
                    // Ignorar si la entrada no es un número válido temporalmente
                }

                return "" // Rechazar la entrada
            }
        }

        etHora.filters = arrayOf(filter)

        // Initialize RecyclerView and layout manager
        recyclerView = findViewById(R.id.rvAlarmas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Initialize the adapter with the empty list
        adapter = RVAlarmasAdapter(dataList)
        recyclerView.adapter = adapter
        // Initialize the button
        addElementButton = findViewById(R.id.btnAddNewAlarm)
        // Set click listener for the button
        addElementButton.setOnClickListener {
            val newElement = "Element ${dataList.size + 1}" // Create new element string
            adapter.addElement(newElement) // Use the adapter's addElement method
        }
    }
}