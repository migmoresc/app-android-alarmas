package com.migmoresc.alarmas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AlarmaAdapter(
    private val dataList: MutableList<Alarma>,
    private val onItemButtonClick: (Int, String, AlarmaViewHolder) -> Unit
) : RecyclerView.Adapter<AlarmaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)
        return AlarmaViewHolder(view, onItemButtonClick)
    }

    override fun onBindViewHolder(holder: AlarmaViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    fun addElement(element: Alarma): Boolean {
        try {
            val dataListAux: MutableList<Alarma> = dataList.toMutableList()
            dataList.clear()
            dataList.add(element)
            dataList.addAll(dataListAux)
            notifyItemInserted(0)
            return true
        } catch (e: Exception) {
            println("Ocurrió un error al intentar añadir la alarma: ${e.message}")
            return false
        }
    }

    override fun getItemCount() = dataList.size

}