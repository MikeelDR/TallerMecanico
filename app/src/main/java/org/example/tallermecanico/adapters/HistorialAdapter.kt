package org.example.tallermecanico.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.example.tallermecanico.R // Asegúrate de que el paquete R sea el correcto según la estructura de tu proyecto.
import org.example.tallermecanico.data.models.HistorialReparacion

class HistorialAdapter(private val listaHistorial: List<HistorialReparacion>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvServicio: TextView = itemView.findViewById(R.id.tvServicio)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = listaHistorial[position]
        holder.tvServicio.text = item.servicio
        holder.tvFecha.text = "Fecha: ${item.fecha}"
        holder.tvEstado.text = "Estado: ${item.estado}"
    }

    override fun getItemCount(): Int = listaHistorial.size
}
