package org.example.tallermecanico.ui.theme.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.example.tallermecanico.R
import org.example.tallermecanico.data.models.Mensaje

class ChatAdapter : ListAdapter<Mensaje, RecyclerView.ViewHolder>(Mensaje.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mensaje, parent, false)
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensaje = getItem(position)
        (holder as MensajeViewHolder).bind(mensaje)
    }

    inner class MensajeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvMensaje: TextView = itemView.findViewById(R.id.tvMensaje)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)

        fun bind(mensaje: Mensaje) {
            tvMensaje.text = mensaje.mensaje
            // Convertir el Timestamp a Date y luego a String
            tvFecha.text = mensaje.fecha_hora?.toDate()?.toString()
        }
    }
}

