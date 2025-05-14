package com.migmoresc.alarmas

import android.text.InputFilter
import android.text.Spanned

object InputFilterHelper {

    // Filter to allow only digits
    fun allowDigitsOnly(): InputFilter {
        return InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[0-9]*"))) source else ""
        }
    }

    // Filter to allow only letters
    fun allowLettersOnly(): InputFilter {
        return InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[a-zA-Z]*"))) source else ""
        }
    }

    // Filter to limit the length of the input
    fun maxLengthFilter(maxLength: Int): InputFilter {
        return InputFilter.LengthFilter(maxLength)
    }

    // Filter to allow only alphanumeric characters
    fun allowAlphanumeric(): InputFilter {
        return InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[a-zA-Z0-9]*"))) source else ""
        }
    }

    //
    fun numerosDel0Al(numIncluido: Int):InputFilter
    {
        return object : InputFilter {
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
                    if (input in 0..numIncluido) {
                        return null // Aceptar la entrada
                    }
                } catch (e: NumberFormatException) {
                    // Ignorar si la entrada no es un número válido temporalmente
                }

                return "" // Rechazar la entrada
            }
        }
    }
}
