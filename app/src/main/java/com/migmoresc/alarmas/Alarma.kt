package com.migmoresc.alarmas


import android.os.CountDownTimer

data class Alarma(
    val uuid: String,
    var horas: Int,
    var minutos: Int,
    var segundos: Int,
    var estaParado: Boolean,
    var tiempoComienzo: Long,
    var tiempoFin: Long,
    @Transient var sonido: SoundRepeater?
)