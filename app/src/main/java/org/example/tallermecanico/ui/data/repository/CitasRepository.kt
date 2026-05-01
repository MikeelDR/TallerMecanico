package org.example.tallermecanico.ui.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.example.tallermecanico.data.models.Cita
import org.example.tallermecanico.data.models.EstadoVehiculo
import org.example.tallermecanico.data.models.Servicio
import org.example.tallermecanico.data.models.VehiculoRegistrado
import org.example.tallermecanico.viewmodel.EstadoCita
import java.time.LocalDate
import java.util.Calendar
import java.util.Date


open class CitasRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val serviciosCollection = firestore.collection("servicios")
    private val vehiculosCollection = firestore.collection("vehiculos")
    private val vehiculosRegistradosCollection = firestore.collection("vehiculos_registrados")
    private val notificacionesCollection = firestore.collection("notificaciones")
    private val vehiculosRepository = VehiculosRepository()
    private val serviciosRepository = ServiciosRepository()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser
    private val citasCollection = db.collection("citas")
    private val userId = currentUser?.uid
    private val citaId = currentUser?.uid
    private val servicioId = currentUser?.uid
    private val vehiculoId = currentUser?.uid
    private val fechaCreacion = currentUser?.uid
    private val notas = currentUser?.uid
    private val nombreCliente = currentUser?.displayName
    private val email = currentUser?.email
    private val displayName = currentUser?.displayName
    private val photoUrl = currentUser?.photoUrl
    private val phoneNumber = currentUser?.phoneNumber
    private val isEmailVerified = currentUser?.isEmailVerified
    private val isAnonymous = currentUser?.isAnonymous
    private val metadata = currentUser?.metadata
    private val providerId = currentUser?.providerId
    private val uid = currentUser?.uid

    // ========== MÉTODOS PARA CITAS ==========

    // Obtiene las citas pendientes como Flow
    fun getCitasPendientesFlow(): Flow<List<Cita>> = callbackFlow {
        val subscription = citasCollection
            .whereEqualTo("estado", "pendiente")
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val citas = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Cita::class.java)
                } ?: emptyList()

                trySend(citas)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun obtenerCitas(): List<Cita> {
        val snapshot = citasCollection.get().await()
        return snapshot.documents.mapNotNull { doc ->
            try {
                val cita = doc.toObject(Cita::class.java)
                cita?.id = doc.id
                cita
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun marcarComoCompletada(citaId: String) {
        citasCollection.document(citaId)
            .update("estado", "completada")
            .await()
    }

    // Obtiene las citas pendientes de forma suspendida
    suspend fun getCitasPendientes(): List<Cita> = withContext(Dispatchers.IO) {
        val snapshot = citasCollection.whereEqualTo("estado", "pendiente").get().await()
        snapshot.toObjects(Cita::class.java)
    }

    // Obtiene las citas aceptadas
    suspend fun getCitasAceptadas(): List<Cita> = withContext(Dispatchers.IO) {
        val snapshot = citasCollection.whereEqualTo("estado", "aceptada").get().await()
        snapshot.toObjects(Cita::class.java)
    }

    // Obtiene todas las citas como Flow
    fun getAllCitasFlow(): Flow<List<Cita>> = callbackFlow {
        val subscription = citasCollection
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val citas = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Cita::class.java)
                } ?: emptyList()

                trySend(citas)
            }

        awaitClose { subscription.remove() }
    }

    // Obtiene todas las citas (no solo como Flow)
    suspend fun getAllCitas(): List<Cita> = withContext(Dispatchers.IO) {
        val snapshot = citasCollection.get().await()
        snapshot.toObjects(Cita::class.java)
    }

    // Obtiene las citas de un usuario específico
    suspend fun getCitasByUserId(userId: String): List<Cita> = withContext(Dispatchers.IO) {
        val snapshot = citasCollection.whereEqualTo("userId", userId).get().await()
        snapshot.toObjects(Cita::class.java)
    }

    // Obtiene las citas de un usuario específico como Flow
    fun getCitasByUserIdFlow(userId: String): Flow<List<Cita>> = callbackFlow {
        val subscription = citasCollection
            .whereEqualTo("userId", userId)
            .orderBy("fechaCreacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val citas = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Cita::class.java)
                } ?: emptyList()

                trySend(citas)
            }

        awaitClose { subscription.remove() }
    }

    // Obtiene una cita por su ID
    open suspend fun getCitaById(citaId: String): Cita? = withContext(Dispatchers.IO) {
        try {
            val document = citasCollection.document(citaId).get().await()
            document.toObject(Cita::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Crear una nueva cita
    suspend fun crearCita(cita: Cita): String = withContext(Dispatchers.IO) {
        val citaId = if (cita.id.isEmpty()) {
            citasCollection.document().id
        } else {
            cita.id
        }

        val nuevaCita = cita.copy(
            id = citaId,
            fechaCreacion = System.currentTimeMillis().toString(),
            estado = "pendiente"
        )

        citasCollection.document(citaId).set(nuevaCita).await()
        citaId
    }

    // Aceptar una cita y asignar vehículo y servicio
    suspend fun aceptarCita(citaId: String, vehiculoId: String, servicioId: String) =
        withContext(Dispatchers.IO) {
            val updates = hashMapOf<String, Any>(
                "estado" to "aceptada",
                "vehiculoId" to vehiculoId,
                "servicioId" to servicioId
            )

            citasCollection.document(citaId).update(updates).await()

            // Obtener la cita actualizada
            val citaDocument = citasCollection.document(citaId).get().await()
            val cita = citaDocument.toObject(Cita::class.java)

            // Crear estado del vehículo
            if (cita != null) {
                crearEstadoVehiculo(
                    vehiculoId = vehiculoId,
                    citaId = citaId,
                    userId = cita.userId,
                    marca = cita.marca,
                    modelo = cita.modelo,
                    anio = cita.anio,
                    placa = cita.placa,
                    servicioId = servicioId
                )

                // Notificar al cliente sobre la aceptación
                enviarNotificacionCliente(
                    clienteId = cita.userId,
                    titulo = "Cita Aceptada",
                    mensaje = "Tu cita para el vehículo ${cita.marca} ${cita.modelo} ha sido aceptada"
                )
            }
        }

    // Rechazar una cita
    suspend fun rechazarCita(citaId: String) = withContext(Dispatchers.IO) {
        val cita = getCitaById(citaId)
        citasCollection.document(citaId).update("estado", "rechazada").await()

        if (cita != null) {
            enviarNotificacionCliente(
                clienteId = cita.userId,
                titulo = "Cita Rechazada",
                mensaje = "Lo sentimos, tu cita para el vehículo ${cita.marca} ${cita.modelo} ha sido rechazada"
            )
        }
    }

    // Completar una cita
    suspend fun completarCita(citaId: String) = withContext(Dispatchers.IO) {
        val cita = getCitaById(citaId)
        citasCollection.document(citaId).update("estado", "completada").await()

        if (cita != null) {
            enviarNotificacionCliente(
                clienteId = cita.userId,
                titulo = "Servicio Completado",
                mensaje = "El servicio para tu vehículo ${cita.marca} ${cita.modelo} ha sido completado"
            )
        }
    }

    // ========== MÉTODOS PARA SERVICIOS ==========

    // Obtiene todos los servicios disponibles como Flow
    fun getAllServiciosFlow(): Flow<List<Servicio>> = callbackFlow {
        val subscription = serviciosCollection
            .orderBy("nombre")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val servicios = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Servicio::class.java)
                } ?: emptyList()

                trySend(servicios)
            }

        awaitClose { subscription.remove() }
    }

    // Obtiene todos los servicios disponibles
    suspend fun getAllServicios(): List<Servicio> = withContext(Dispatchers.IO) {
        val snapshot = serviciosCollection.get().await()
        snapshot.toObjects(Servicio::class.java)
    }

    // Obtiene un servicio por su ID
    suspend fun getServicioPorId(servicioId: String): Servicio? = withContext(Dispatchers.IO) {
        val document = serviciosCollection.document(servicioId).get().await()
        document.toObject(Servicio::class.java)
    }

    // Obtiene servicios por categoría
    suspend fun getServiciosPorCategoria(categoria: String): List<Servicio> =
        withContext(Dispatchers.IO) {
            val snapshot = serviciosCollection.whereEqualTo("categoria", categoria).get().await()
            snapshot.toObjects(Servicio::class.java)
        }

    // ========== MÉTODOS PARA VEHÍCULOS ==========

    // Crea el estado inicial de un vehículo
    suspend fun crearEstadoVehiculo(
        vehiculoId: String,
        citaId: String,
        userId: String,
        marca: String,
        modelo: String,
        anio: String,
        placa: String,
        servicioId: String
    ) = withContext(Dispatchers.IO) {
        val servicio = serviciosRepository.getServicioPorId(servicioId)

        val estadoVehiculo = EstadoVehiculo(
            id = vehiculoId,
            citaId = citaId,
            userId = userId,
            marca = marca,
            modelo = modelo,
            anio = anio,
            placa = placa,
            servicio = servicio?.nombre,
            estadoActual = "En espera",
            porcentajeCompletado = 0,
            fechaCreacion = System.currentTimeMillis(),
            fechaActualizacion = System.currentTimeMillis()
        )

        vehiculosCollection.document(vehiculoId).set(estadoVehiculo).await()
    }

    // Obtiene el estado de un vehículo
    suspend fun getEstadoVehiculo(vehiculoId: String): EstadoVehiculo =
        withContext(Dispatchers.IO) {
            val document = vehiculosCollection.document(vehiculoId).get().await()
            document.toObject(EstadoVehiculo::class.java) ?: EstadoVehiculo(id = vehiculoId)
        }

    // Obtiene el estado de un vehículo como Flow
    fun getEstadoVehiculoFlow(vehiculoId: String): Flow<EstadoVehiculo?> = callbackFlow {
        val subscription = vehiculosCollection.document(vehiculoId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val estadoVehiculo = snapshot?.toObject(EstadoVehiculo::class.java)
                trySend(estadoVehiculo)
            }

        awaitClose { subscription.remove() }
    }

    // Actual el estado de un vehículo
    suspend fun actualizarEstadoVehiculo(
        vehiculoId: String,
        estadoVehiculo: org.example.tallermecanico.viewmodel.EstadoVehiculo
    ) = withContext(Dispatchers.IO) {
        val updates = hashMapOf<String, Any>(
            "diagnostico" to (estadoVehiculo.diagnostico ?: ""),
            "reparacionesRealizadas" to (estadoVehiculo.reparacionesRealizadas ?: ""),
            "piezasReemplazadas" to (estadoVehiculo.piezasReemplazadas ?: ""),
            "porcentajeCompletado" to (estadoVehiculo.porcentajeCompletado ?: 0),
            "estadoActual" to (estadoVehiculo.estadoActual ?: "En espera"),
            "comentarios" to (estadoVehiculo.comentarios ?: ""),
            "fechaActualizacion" to System.currentTimeMillis()
        )

        val documentRef = vehiculosCollection.document(estadoVehiculo.id.toString())
        documentRef.update(updates).await()

        // Notificar al cliente sobre el cambio de estado
        val vehiculo = getEstadoVehiculo(estadoVehiculo.id.toString())
        notificarCliente(estadoVehiculo.id.toString(), estadoVehiculo.estadoActual ?: "")
    }

    // Obtiene vehículos asignados a un técnico
    suspend fun getVehiculosPorTecnico(tecnicoId: String): List<EstadoVehiculo> =
        withContext(Dispatchers.IO) {
            val snapshot = vehiculosCollection.whereEqualTo("tecnicoId", tecnicoId).get().await()
            snapshot.toObjects(EstadoVehiculo::class.java)
        }

    // Obtiene vehículos por usuario como Flow
    fun getVehiculosPorUsuarioFlow(userId: String): Flow<List<EstadoVehiculo>> = callbackFlow {
        val subscription = vehiculosCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val vehiculos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(EstadoVehiculo::class.java)
                } ?: emptyList()

                trySend(vehiculos)
            }

        awaitClose { subscription.remove() }
    }

    // Obtiene vehículos por usuario
    suspend fun getVehiculosPorUsuario(userId: String): List<EstadoVehiculo> =
        withContext(Dispatchers.IO) {
            val snapshot = vehiculosCollection.whereEqualTo("userId", userId).get().await()
            snapshot.toObjects(EstadoVehiculo::class.java)
        }

    // ========== MÉTODOS PARA VEHÍCULOS REGISTRADOS ==========

    // Registra un nuevo vehículo en el sistema
    suspend fun registrarVehiculo(vehiculo: org.example.tallermecanico.viewmodel.VehiculoRegistrado) =
        withContext(Dispatchers.IO) {
            vehiculosRegistradosCollection.document(vehiculo.id).set(vehiculo).await()
        }

    // Obtiene un vehículo registrado por su ID
    fun getVehiculoRegistradoFlow(vehiculoId: String): Flow<VehiculoRegistrado?> {
        val result = MutableStateFlow<VehiculoRegistrado?>(null)

        vehiculosRegistradosCollection.document(vehiculoId).get()
            .addOnSuccessListener { document ->
                result.value = document.toObject(VehiculoRegistrado::class.java)
            }

        return result
    }

    // Obtiene un vehículo registrado por su ID de forma suspendida
    suspend fun getVehiculoRegistrado(vehiculoId: String): VehiculoRegistrado? =
        withContext(Dispatchers.IO) {
            val document = vehiculosRegistradosCollection.document(vehiculoId).get().await()
            document.toObject(VehiculoRegistrado::class.java)
        }

    // ========== MÉTODOS PARA NOTIFICACIONES ==========

    // Notifica al cliente sobre cambios en el estado del vehículo
    suspend fun notificarCliente(vehiculoId: String, estado: String) = withContext(Dispatchers.IO) {
        // Obtener estado del vehículo
        val estadoVehiculo = getEstadoVehiculo(vehiculoId)

        // Obtener token de notificación del usuario
        val userDocument =
            firestore.collection("users").document(estadoVehiculo.userId ?: "").get().await()
        val notificationToken = userDocument.getString("notificationToken")

        // Si hay un token, enviar notificación push
        if (notificationToken != null) {
            val message = RemoteMessage.Builder(notificationToken)
                .setMessageId(System.currentTimeMillis().toString())
                .setData(
                    mapOf(
                        "title" to "Actualización de tu vehículo",
                        "body" to "Tu vehículo ${estadoVehiculo.marca} ${estadoVehiculo.modelo} ahora está: $estado",
                        "vehiculoId" to vehiculoId
                    )
                )
                .build()

            FirebaseMessaging.getInstance().send(message)
        }

        // Guardar también en la colección de notificaciones
        val notificacion = hashMapOf(
            "clienteId" to estadoVehiculo.userId,
            "titulo" to "Actualización de tu vehículo",
            "mensaje" to "Tu vehículo ${estadoVehiculo.marca} ${estadoVehiculo.modelo} ahora está: $estado",
            "leida" to false,
            "fechaCreacion" to System.currentTimeMillis()
        )

        notificacionesCollection.add(notificacion).await()
    }

    // Envía una notificación personalizada al cliente
    suspend fun enviarNotificacionCliente(clienteId: String, titulo: String, mensaje: String) =
        withContext(Dispatchers.IO) {
            // Guardar en la colección de notificaciones
            val notificacion = hashMapOf(
                "clienteId" to clienteId,
                "titulo" to titulo,
                "mensaje" to mensaje,
                "leida" to false,
                "fechaCreacion" to System.currentTimeMillis()
            )

            notificacionesCollection.add(notificacion).await()

            // Si hay un token, enviar notificación push
            val userDocument = firestore.collection("users").document(clienteId).get().await()
            val notificationToken = userDocument.getString("notificationToken")

            if (notificationToken != null) {
                val message = RemoteMessage.Builder(notificationToken)
                    .setMessageId(System.currentTimeMillis().toString())
                    .setData(
                        mapOf(
                            "title" to titulo,
                            "body" to mensaje
                        )
                    )
                    .build()

                FirebaseMessaging.getInstance().send(message)
            }
        }

    fun agendarCita(cita: Cita, callback: (Boolean) -> Unit) {
        db.collection("citas").document(cita.id).set(cita)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }



    fun obtenerCitasPorUsuario(uid: String, callback: (List<Cita>) -> Unit) {
        db.collection("citas").whereEqualTo("usuarioId", uid).get()
            .addOnSuccessListener { result ->
                val citas = result.toObjects(Cita::class.java)
                callback(citas)
            }
            .addOnFailureListener { callback(emptyList()) }
    }

    // Métodos adicionales requeridos por CitasViewModel

    // Obtener citas por fecha
    suspend fun getCitasPorFecha(fecha: Date): List<Cita> = withContext(Dispatchers.IO) {
        // Calculamos el inicio y fin del día
        val calendar = Calendar.getInstance()
        calendar.time = fecha
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val inicioDia = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val finDia = calendar.timeInMillis

        val snapshot = citasCollection
            .whereGreaterThanOrEqualTo("fechaCreacion", inicioDia)
            .whereLessThanOrEqualTo("fechaCreacion", finDia)
            .get()
            .await()

        snapshot.toObjects(Cita::class.java)
    }

    // Agregar una cita desde ViewModel
    suspend fun agregarCita(cita: Cita): String = withContext(Dispatchers.IO) {
        // Convertir del modelo ViewModel al modelo de datos
        val usuarioId = ""
        val nuevaCita = Cita(
            id = cita.id,
            userId = cita.clienteId,
            nombre = cita.nombreCliente,
            telefono = TODO(),
            email = TODO(),
            marca = cita.marcaVehiculo ?: "",
            modelo = cita.modeloVehiculo,
            anio = cita.anoVehiculo ?: "",
            placa = cita.placa,
            kilometraje = TODO(),
            descripcionProblema = cita.descripcionProblema ?: "",
            fechaSolicitud = TODO(),
            fechaDeseada = TODO(),
            horaDeseada = TODO(),
            vehiculoId = TODO(),
            servicioId = TODO(),
            nombreCliente = TODO(),
            marcaVehiculo = TODO(),
            modeloVehiculo = TODO(),
            anioVehiculo = TODO(),
            anoVehiculo = TODO(),
            clienteId = TODO(),
            servicio = cita.servicio,
            notas = cita.notas ?: "",
            diagnostico = TODO(),
            hora = cita.hora,
            needsSync = TODO(),
            syncId = TODO(),
            selectedDate = TODO(),
            idSeguimiento = cita.idSeguimiento ?: "",
            usuarioId = usuarioId
        )

        val citaId = crearCita(nuevaCita)
        citaId
    }

    // Actualizar una cita existente
    suspend fun actualizarCita(cita: Cita) = withContext(Dispatchers.IO) {
        val updates = hashMapOf<String, Any>(
            "nombre" to cita.nombreCliente,
            "marca" to (cita.marcaVehiculo ?: ""),
            "modelo" to cita.modeloVehiculo,
            "anio" to (cita.anoVehiculo ?: ""),
            "placa" to cita.placa,
            "hora" to cita.hora,
            "servicio" to cita.servicio,
            "notas" to (cita.notas ?: ""),
            "descripcionProblema" to (cita.descripcionProblema ?: "")
        )

        citasCollection.document(cita.id).update(updates).await()
    }

    // Eliminar una cita
    suspend fun eliminarCita(citaId: String) = withContext(Dispatchers.IO) {
        citasCollection.document(citaId).delete().await()
    }

    // Actualizar estado de una cita
    suspend fun actualizarEstadoCita(
        citaId: String,
        nuevoEstado: EstadoCita,
        servicioId: String = ""
    ) = withContext(Dispatchers.IO) {
        val updates = hashMapOf<String, Any>(
            "estado" to nuevoEstado.name.toLowerCase()
        )

        // Si se proporciona un ID de servicio, también lo actualizamos
        if (servicioId.isNotEmpty()) {
            updates["servicioId"] = servicioId
        }

        citasCollection.document(citaId).update(updates).await()
    }

    // Obtener estado de un vehículo por ID para ViewModel
    suspend fun getEstadoVehiculoById(vehiculoId: String): org.example.tallermecanico.viewmodel.EstadoVehiculo? =
        withContext(Dispatchers.IO) {
            val document = vehiculosCollection.document(vehiculoId).get().await()
            val estadoVehiculo = document.toObject(EstadoVehiculo::class.java)

            // Convertir del modelo de datos al modelo ViewModel
            estadoVehiculo?.let {
                org.example.tallermecanico.viewmodel.EstadoVehiculo(
                    idSeguimiento = it.id,
                    vehiculo = "${it.marca} ${it.modelo}",
                    cliente = it.userId
                        ?: "", // Asumiendo que userId contiene el nombre del cliente o su ID
                    estadoActual = it.estadoActual ?: "",
                    descripcion = it.estadoActual ?: "",
                    fechaIngreso = it.fechaCreacion.toString(),
                    porcentajeCompletado = it.porcentajeCompletado ?: 0,
                    diagnostico = it.diagnostico ?: "",
                    reparacionesRealizadas = it.reparacionesRealizadas ?: "",
                    piezasReemplazadas = it.piezasReemplazadas ?: "",
                    comentarios = it.comentarios ?: "",
                    citaId = it.citaId ?: "",
                    vehiculoId = it.id,
                    userId = it.userId ?: "",
                    marca = it.marca ?: "",
                    modelo = it.modelo ?: "",
                    anio = it.anio ?: "",
                    placa = it.placa ?: "",
                    servicio = it.servicio ?: ""
                )
            }
        }

    // NUEVOS MÉTODOS SOLICITADOS

    /**
     * Obtiene citas por fecha específica.
     * @param fecha La fecha para la cual se desean obtener las citas
     * @return Lista de citas para la fecha especificada
     */
    suspend fun obtenerCitasPorFecha(fecha: LocalDate?): List<Cita> = withContext(Dispatchers.IO) {
        if (fecha == null) {
            return@withContext emptyList()
        }

        // Convertimos LocalDate a Calendar
        val calendar = Calendar.getInstance()
        // Extraemos año, mes y día de LocalDate
        calendar.set(fecha.year, fecha.monthValue - 1, fecha.dayOfMonth, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val inicioDia = calendar.timeInMillis

        // Configuramos el final del día
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val finDia = calendar.timeInMillis

        val snapshot = citasCollection
            .whereGreaterThanOrEqualTo("fechaDeseada", inicioDia)
            .whereLessThanOrEqualTo("fechaDeseada", finDia)
            .get()
            .await()

        snapshot.documents.mapNotNull { doc ->
            try {
                val cita = doc.toObject(Cita::class.java)
                cita?.id = doc.id
                cita
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Obtiene todos los servicios disponibles.
     * @return Lista de servicios disponibles
     */
    suspend fun obtenerServicios(): List<Servicio> = withContext(Dispatchers.IO) {
        val snapshot = serviciosCollection.orderBy("nombre").get().await()
        snapshot.documents.mapNotNull { doc ->
            try {
                val servicio = doc.toObject(Servicio::class.java)
                servicio?.id = doc.id
                servicio
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Actualiza el estado de una cita.
     * @param citaId ID de la cita a actualizar
     * @param estadoCita Nuevo estado para la cita
     */
    suspend fun actualizarEstadoCita(citaId: String, estadoCita: String) = withContext(Dispatchers.IO) {
        try {
            val updates = hashMapOf<String, Any>(
                "estado" to estadoCita,
                "fechaActualizacion" to System.currentTimeMillis()
            )

            citasCollection.document(citaId).update(updates).await()

            // Obtener la cita actualizada para enviar notificación
            val cita = getCitaById(citaId)

            if (cita != null) {
                // Enviar notificación al cliente sobre el cambio de estado
                enviarNotificacionCliente(
                    clienteId = cita.userId,
                    titulo = "Estado de cita actualizado",
                    mensaje = "Tu cita para el vehículo ${cita.marca} ${cita.modelo} ha cambiado a: $estadoCita"
                )
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Finaliza un servicio, actualizando tanto la cita como el estado del vehículo.
     * @param citaId ID de la cita a finalizar
     * @param detallesFinalizacion Detalles opcionales sobre la finalización del servicio
     * @return true si la operación fue exitosa, false en caso contrario
     */
    suspend fun finalizarServicio(
        citaId: String,
        detallesFinalizacion: String = ""
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // 1. Obtener la cita
            val cita = getCitaById(citaId)

            if (cita == null) {
                return@withContext false
            }

            // 2. Actualizar estado de la cita a completada
            val citaUpdates = hashMapOf<String, Any>(
                "estado" to "completada",
                "fechaActualizacion" to System.currentTimeMillis()
            )

            if (detallesFinalizacion.isNotEmpty()) {
                citaUpdates["notasFinalizacion"] = detallesFinalizacion
            }

            citasCollection.document(citaId).update(citaUpdates).await()

            // 3. Si la cita tiene un vehículo asociado, actualizar su estado
            if (cita.vehiculoId?.isNotEmpty() == true) {
                val vehiculoId = cita.vehiculoId
                val estadoVehiculo = getEstadoVehiculo(vehiculoId)

                val vehiculoUpdates = hashMapOf<String, Any>(
                    "estadoActual" to "Servicio completado",
                    "porcentajeCompletado" to 100,
                    "fechaActualizacion" to System.currentTimeMillis()
                )

                if (detallesFinalizacion.isNotEmpty()) {
                    vehiculoUpdates["comentarios"] = detallesFinalizacion
                }

                vehiculosCollection.document(vehiculoId).update(vehiculoUpdates).await()
            }

            // 4. Enviar notificación al cliente
            enviarNotificacionCliente(
                clienteId = cita.userId,
                titulo = "Servicio Finalizado",
                mensaje = "El servicio para tu vehículo ${cita.marca} ${cita.modelo} ha sido completado satisfactoriamente."
            )

            true
        } catch (e: Exception) {
            false
        }
    }
}