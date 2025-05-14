package com.migmoresc.alarmas

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class AlarmaViewHolder(
    itemView: View,
    private val onItemButtonClick: (Int, String, AlarmaViewHolder) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val rviFramePadre: LinearLayout = itemView.findViewById(R.id.rviFramePadre)
    private val rviFrameA: FrameLayout = itemView.findViewById(R.id.rviFrameA)
    private val rviFrameB: FrameLayout = itemView.findViewById(R.id.rviFrameB)
    private val rviFrameC: LinearLayout = itemView.findViewById(R.id.rviFrameC)
    private val tvAlarma: TextView = itemView.findViewById(R.id.rviTextView)
    private val tvAlarmaControl: TextView = itemView.findViewById(R.id.rviTextViewControl)
    private val rviIBAlarmRemove: ImageButton = itemView.findViewById(R.id.rviIBAlarmRemove)
    private val rviIBAlarmEdit: ImageButton = itemView.findViewById(R.id.rviIBAlarmEdit)
    val rviIBAlarm: ImageButton = itemView.findViewById(R.id.rviIBAlarm)

    @SuppressLint("DefaultLocale", "SetTextI18n")
    fun bind(alarma: Alarma) {
        val tfH: String = String.format("%02d", alarma.horas)
        val tfM: String = String.format("%02d", alarma.minutos)
        val tfS: String = String.format("%02d", alarma.segundos)

        tvAlarmaControl.text = "$tfH:$tfM:$tfS"

        if (alarma.estaParado) {
            tvAlarma.text = "$tfH:$tfM:$tfS"
            rviIBAlarm.setImageResource(R.drawable.ic_alarm_off)
            rviIBAlarm.setColorFilter(ContextCompat.getColor(itemView.context, R.color.white))
        } else {
            rviIBAlarm.setImageResource(R.drawable.ic_alarm_on)
            rviIBAlarm.setColorFilter(ContextCompat.getColor(itemView.context, R.color.green))
        }

        rviIBAlarm.setOnClickListener {
            clickListener("toggle")
        }

        rviIBAlarmEdit.setOnClickListener {
            clickListener("editar")
        }

        rviIBAlarmRemove.setOnClickListener {
            clickListener("borrar")
        }

        calcularAnchosYAltos()
    }

    private fun calcularAnchosYAltos() {
        rviFrameC.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val metrics = Resources.getSystem().displayMetrics
        val anchoBotones:Int = rviFrameC.measuredWidth

        rviFramePadre.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val anchoPadre:Int = rviFramePadre.measuredWidth

        rviFrameA.layoutParams.width = ((anchoPadre - anchoBotones) / 2).toInt()
        rviFrameB.layoutParams.width = ((anchoPadre - anchoBotones) / 2).toInt()
    }

    private fun clickListener(accion: String) {
        val currentPosition = bindingAdapterPosition
        if (currentPosition != RecyclerView.NO_POSITION) {
            onItemButtonClick(currentPosition, accion, this)
        }
    }
}