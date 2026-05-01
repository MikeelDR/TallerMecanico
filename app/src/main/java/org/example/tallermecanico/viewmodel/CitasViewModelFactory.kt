package org.example.tallermecanico.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.example.tallermecanico.ui.data.repository.CitasRepository
import org.example.tallermecanico.ui.data.repository.VehiculosRepository
import javax.inject.Inject

/**
 * Factory para crear instancias de CitasViewModel con sus dependencias.
 * Esta clase se utiliza cuando se necesita proporcionar dependencias al ViewModel
 * en tiempo de creación.
 */
@Suppress("UNCHECKED_CAST")
class CitasViewModelFactory @Inject constructor(
    private val citasRepository: CitasRepository,
    private val vehiculosRepository: VehiculosRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CitasViewModel::class.java)) {
            return CitasViewModel(citasRepository, vehiculosRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}