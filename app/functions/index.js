const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendReminderNotification = functions.firestore
    .document('citas/{citaId}')
    .onUpdate((change, context) => {
        const cita = change.after.data();
        const token = cita.token;  // Token almacenado en la base de datos del cliente

        const message = {
            notification: {
                title: "Recordatorio de Cita",
                body: `Tienes una cita programada para ${cita.fecha} a las ${cita.hora}.`,
            },
            token: token,
        };

        // Envía la notificación
        return admin.messaging().send(message)
            .then((response) => {
                console.log("Notificación enviada: ", response);
            })
            .catch((error) => {
                console.error("Error al enviar notificación: ", error);
            });
    });
