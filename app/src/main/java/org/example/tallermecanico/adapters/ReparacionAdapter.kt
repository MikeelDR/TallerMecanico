package org.example.tallermecanico.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import org.example.tallermecanico.R
import org.example.tallermecanico.data.models.Reparacion
import org.example.tallermecanico.databinding.ItemReparacionBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ReparacionAdapter : ListAdapter<Reparacion, ReparacionAdapter.ReparacionViewHolder>(ReparacionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReparacionViewHolder {
        val binding = ItemReparacionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReparacionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReparacionViewHolder, position: Int) {
        val reparacion = getItem(position)
        holder.bind(reparacion)
    }

    class ReparacionViewHolder(private val binding: ItemReparacionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reparacion: Reparacion) {
            // Llenamos los campos con la información de la reparación
            binding.tvDescripcion.text = reparacion.descripcion

            // Agregamos el ID del vehículo (asumiendo que tienes este TextView en tu layout)
            binding.tvVehiculoId.text = "Vehículo: ${reparacion.vehiculo_id}"

            // Agregamos el ID del mecánico (asumiendo que tienes este TextView en tu layout)
            binding.tvMecanicoId.text = "Mecánico: ${reparacion.mecanico_uid}"

            // Formateamos la fecha de reparación si está disponible
            val fechaFormateada = formatDate(reparacion.fecha_reparacion)
            binding.tvFecha.text = "Fecha: $fechaFormateada"

            // Mostramos el costo
            binding.tvCosto.text = "Costo: $${reparacion.costo}"

            // Mostramos el estado de la reparación
            binding.tvEstado.text = "Estado: ${reparacion.estado}"

            // Puedes aplicar colores diferentes dependiendo del estado de la reparación
            when (reparacion.estado) {
                "Completada" -> binding.tvEstado.setTextColor(binding.root.context.getColor(R.color.green))
                "Pendiente" -> binding.tvEstado.setTextColor(binding.root.context.getColor(R.color.orange))
                "En proceso" -> binding.tvEstado.setTextColor(binding.root.context.getColor(R.color.yellow))
                else -> binding.tvEstado.setTextColor(binding.root.context.getColor(R.color.red))
            }

            // También podemos mostrar el ID de la reparación
            binding.tvReparacionId.text = "ID: ${reparacion.id}"
        }

        // Función para formatear la fecha de la reparación (en formato dd/MM/yyyy)
        private fun formatDate(fecha: Timestamp?): String {
            return if (fecha != null) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.format(fecha.toDate())
            } else {
                "Fecha no disponible"
            }
        }
    }

    // DiffUtil Callback para comparar las listas y optimizar las actualizaciones
    class ReparacionDiffCallback : DiffUtil.ItemCallback<Reparacion>() {
        override fun areItemsTheSame(oldItem: Reparacion, newItem: Reparacion): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reparacion, newItem: Reparacion): Boolean {
            return oldItem == newItem
        }
    }
}