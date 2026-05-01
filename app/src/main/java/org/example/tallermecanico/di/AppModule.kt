// File: org.example.tallermecanico.di.AppModule.kt
package org.example.tallermecanico.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.example.tallermecanico.ui.data.repository.CitasRepositoryImpl
import org.example.tallermecanico.ui.data.repository.VehiculosRepositoryImpl
import org.example.tallermecanico.viewmodel.CitasRepository
import org.example.tallermecanico.viewmodel.VehiculosRepository
import org.example.tallermecanico.viewmodel.CitasViewModel
import org.example.tallermecanico.viewmodel.VehiculosViewModel
import org.example.tallermecanico.viewmodel.ActualizarEstadoViewModel
import javax.inject.Singleton

/**
 * Módulo Hilt para proporcionar dependencias relacionadas con repositorios
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCitasRepository(): CitasRepository {
        return CitasRepositoryImpl(
            firestore = TODO()
        )
    }

    @Provides
    @Singleton
    fun provideVehiculosRepository(): VehiculosRepository {
        return VehiculosRepositoryImpl(
            firestore = TODO()
        )
    }
}
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCitasViewModel(
        citasRepository: CitasRepository,
        vehiculosRepository: VehiculosRepository,
    ): CitasViewModel {
        return CitasViewModel(citasRepository, vehiculosRepository)
    }

    private fun CitasViewModel(
        repository: CitasRepository,
        VehiculosRepository: VehiculosRepository
    ): CitasViewModel {
        TODO("Not yet implemented")
    }



    @Provides
    @Singleton
    fun provideActualizarEstadoViewModel(
        vehiculosRepository: VehiculosRepository
    ): ActualizarEstadoViewModel {
        return ActualizarEstadoViewModel(vehiculosRepository)
    }
}