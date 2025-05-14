package com.migmoresc.alarmas

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import kotlinx.coroutines.*

class SoundRepeater(private val context: Context, private val intervalMillis: Long) {

    private var job: Job? = null
    private var ringtone: Ringtone? = null

    init {
        // Register this instance
        activeRepeaters.add(this)
    }

    fun startRepeatingSound() {
        if (job?.isActive == true) return

        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                playSystemSound()
                delay(intervalMillis)
            }
        }
    }

    private fun playSystemSound() {
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ringtone = RingtoneManager.getRingtone(context, notification)
        ringtone?.play()
    }

    fun stopRepeatingSound() {
        job?.cancel()
        ringtone?.stop()
    }

    fun destroy() {
        stopRepeatingSound()
        activeRepeaters.remove(this)
    }

    companion object {
        private val activeRepeaters = mutableListOf<SoundRepeater>()

        fun stopAll() {
            for (repeater in activeRepeaters) {
                repeater.stopRepeatingSound()
            }
        }

        fun destroyAll() {
            for (repeater in activeRepeaters.toList()) {
                repeater.destroy()
            }
        }
    }
}
