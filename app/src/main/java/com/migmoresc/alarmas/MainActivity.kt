package com.migmoresc.alarmas

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearSmoothScroller
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

//https://www.flaticon.com/free-icon/bell_1827314?term=alarm&related_id=1827314

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlarmaAdapter
    private val dataList = mutableListOf<Alarma>()
    private lateinit var addAlarmButton: ImageButton
    private lateinit var dialog: AlertDialog
    private lateinit var acepptDialog: ImageButton
    private lateinit var cancelDialog: ImageButton
    private lateinit var etHours: EditText
    private lateinit var etMinutes: EditText
    private lateinit var etSeconds: EditText
    private var seEstaEditando: Int = -1

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars =
            false // false = light icons, true = dark icons
        insetsController.isAppearanceLightNavigationBars = false

        initComponents()
        initUI()
        initListeners()
    }

    private fun initComponents() {
        recyclerView = findViewById(R.id.rvAlarmas)
        addAlarmButton = findViewById(R.id.btnAddNewAlarm)
        dialog = createDialog()
    }

    private fun createDialog(): AlertDialog {
        val dialogView = layoutInflater.inflate(R.layout.custom_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        //se tiene que inicializar las variables aqui, no se puede desde fuera sin el dialogview
        acepptDialog = dialogView.findViewById(R.id.ibAceptar)
        cancelDialog = dialogView.findViewById(R.id.ibCancelar)
        return dialog
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initUI() {
        adapter = AlarmaAdapter(dataList) { position, accion, itemView ->
            botonPulsadoItemRecyclerView(position, accion, itemView)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        getDrawable(R.drawable.divider_a)?.let {
            itemDecoration.setDrawable(it)
            recyclerView.addItemDecoration(itemDecoration)
        }
    }

    private fun botonPulsadoItemRecyclerView(
        position: Int,
        accion: String,
        itemView: AlarmaViewHolder
    ) {
        when (accion) {
            "borrar" -> deleteAlarm(position)
            "toggle" -> toggleAlarm(position, itemView)
            "editar" -> editAlarm(position)
        }
    }

    private fun toggleAlarm(position: Int, itemView: AlarmaViewHolder) {

        if (dataList[position].estaParado) {
            dataList[position].estaParado = false
            dataList[position].tiempoComienzo = System.currentTimeMillis() / 1000
            val tiempoSegundos: Long =
                (dataList[position].horas * 60 * 60).toLong() +
                        (dataList[position].minutos * 60) +
                        (dataList[position].segundos)
            dataList[position].tiempoFin = dataList[position].tiempoComienzo + tiempoSegundos

            dataList[position].sonido = SoundRepeater(this, tiempoSegundos * 1000)
            CoroutineScope(Dispatchers.Main).launch {
                delay(tiempoSegundos * 1000) // Delay for 3 seconds (3000 milliseconds)
                dataList[position].sonido?.startRepeatingSound()
            }
        } else {
            dataList[position].sonido?.stopRepeatingSound()
            dataList[position].estaParado = true
            dataList[position].tiempoComienzo = 0
            dataList[position].tiempoFin = 0
        }

        val (iconRes, tintColorRes) = if (dataList[position].estaParado) {
            R.drawable.ic_alarm_off to R.color.white
        } else {
            R.drawable.ic_alarm_on to R.color.green
        }

        itemView.rviIBAlarm.setImageResource(iconRes)
        itemView.rviIBAlarm.setColorFilter(ContextCompat.getColor(this, tintColorRes))
    }

    private fun editAlarm(position: Int) {
        showCustomDialogBox(position)
    }

    private fun initListeners() {
        addAlarmButton.setOnClickListener {
            showCustomDialogBox(null)
        }
        cancelDialog.setOnClickListener {
            dialog.dismiss()
            seEstaEditando = -1
        }
        acepptDialog.setOnClickListener {
            saveAlarm()
        }
    }

    private fun showCustomDialogBox(position: Int?) {
        dialog.show()

        etHours = dialog.findViewById(R.id.etHora)!!
        etMinutes = dialog.findViewById(R.id.etMinutos)!!
        etSeconds = dialog.findViewById(R.id.etSegundos)!!

        etHours.filters = arrayOf(InputFilterHelper.numerosDel0Al(23))
        etMinutes.filters = arrayOf(InputFilterHelper.numerosDel0Al(59))
        etSeconds.filters = arrayOf(InputFilterHelper.numerosDel0Al(59))

        if (position != null) {
            seEstaEditando = position
            etHours.setText(dataList[position].horas.toString())
            etMinutes.setText(dataList[position].minutos.toString())
            etSeconds.setText(dataList[position].segundos.toString())
        } else {
            listOf(etHours, etMinutes, etSeconds).forEach { it.text.clear() }
        }

        etHours.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(etHours, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun saveAlarm() {
        val horas: Int? = etHours.text.toString().toIntOrNull()
        val minutos: Int? = etMinutes.text.toString().toIntOrNull()
        val segundos: Int? = etSeconds.text.toString().toIntOrNull()
        if (horas != null && minutos != null && segundos != null) {
            dialog.dismiss()
            if (seEstaEditando == -1 && adapter.addElement(
                    Alarma(
                        UUID.randomUUID().toString(),
                        horas,
                        minutos,
                        segundos,
                        true,
                        0,
                        0,
                        null,
                    )
                )
            ) {
                smoothScrollToPositionSlow(0, 10.0f)
                Toast.makeText(this, getString(R.string.alarm_added), Toast.LENGTH_LONG).show()
            } else if (seEstaEditando >= 0) {
                try {
                    dataList[seEstaEditando].horas = horas
                    dataList[seEstaEditando].minutos = minutos
                    dataList[seEstaEditando].segundos = segundos
                    dataList[seEstaEditando].estaParado = true
                    adapter.notifyItemChanged(seEstaEditando)
                    seEstaEditando = -1
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.alarm_added_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteAlarm(position: Int) {
        try {
            dataList[position].sonido?.destroy()
            dataList.removeAt(position)
            adapter.notifyItemRemoved(position)
            Toast.makeText(this, getString(R.string.alarm_deleted), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.alarm_deleted_error), Toast.LENGTH_LONG).show()
        }
    }

    @Suppress("SameParameterValue")
    private fun smoothScrollToPositionSlow(position: Int, speedFactor: Float = 2.0f) {
        val layoutManager = recyclerView.layoutManager ?: return

        val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateSpeedPerPixel(displayMetrics: android.util.DisplayMetrics): Float {
                // Default is around 25f / displayMetrics.densityDpi
                // Increase the returned value to slow down scrolling
                return (25f * speedFactor) / displayMetrics.densityDpi
            }
        }
        smoothScroller.targetPosition = position
        layoutManager.startSmoothScroll(smoothScroller)
    }

    override fun onPause() {
        super.onPause()
        SoundRepeater.stopAll()
        val sharedPrefs = getSharedPreferences("lista_guardada", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val json = Gson().toJson(dataList)  // Convert list to JSON
        editor.putString("dataList", json)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        val sharedPrefs = getSharedPreferences("lista_guardada", Context.MODE_PRIVATE)
        val json = sharedPrefs.getString("dataList", null)

        if (json != null) {
            val type = object : TypeToken<MutableList<Alarma>>() {}.type
            dataList.clear()
            dataList.addAll(Gson().fromJson(json, type))
        } else {
            dataList.clear()
        }
        adapter.notifyDataSetChanged() // if needed
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundRepeater.destroyAll() // Fully clean up
    }

}