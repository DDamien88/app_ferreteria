package com.example.appferreteria.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.appferreteria.R;
import com.example.appferreteria.MainActivity; // ⬅️ ¡IMPORTANTE! Cambiado a la Activity Principal
import com.example.appferreteria.modelo.TokenRequest;
import com.example.appferreteria.request.ApiClient;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "FCM_SERVICE";
    private static final String CHANNEL_ID = "FerreteriaChannel";

    /**
     * Llamado si el token FCM del dispositivo se actualiza.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    /**
     * Maneja los mensajes recibidos mientras la app está en primer plano.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        String title = null;
        String body = null;

        // 1. Obtener Título y Cuerpo desde Notification (si existe)
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // 2. Manejar datos de carga útil (data payload)
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // Si no hay campos de Notification, usamos Data como fallback para el cuerpo
            if (title == null || body == null) {
                title = remoteMessage.getData().getOrDefault("title", "Alerta de Ferretería");
                body = remoteMessage.getData().getOrDefault("message", "Mensaje sin cuerpo.");
            }

            handleDataPayload(title, body, remoteMessage.getData());
        } else if (title != null && body != null) {
            // Si solo hay Notification (app en background), mostrar notificación simple
            sendNotification(title, body, null);
        }
    }

    /**
     * Interpreta la carga de datos (Data Payload) y construye la notificación.
     */
    private void handleDataPayload(String title, String body, Map<String, String> data) {

        String action = data.get("action");

        if ("new_product".equals(action)) {
            // Recibido desde ProductosController.cs
            String productIdStr = data.get("productId");

            if (productIdStr != null) {
                try {
                    int productId = Integer.parseInt(productIdStr);

                    // Crear un Intent para abrir la Activity principal que aloja el fragmento
                    // y pasarle el ID del producto como extra.
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("productId", productId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Enviar la notificación con este Intent específico
                    sendNotification(title, body, intent);

                } catch (NumberFormatException e) {
                    Log.e(TAG, "ID de producto inválido: " + productIdStr);
                    sendNotification(title, body, null); // Enviar sin acción de click
                }
            }
        } else {
            // Acción desconocida o nula, enviar notificación simple
            sendNotification(title, body, null);
        }
    }


    /**
     * Envía el token al servidor de la aplicación (Mismo que antes).
     */
    private void sendRegistrationToServer(String tokenFCM) {
        int userId = ApiClient.obtenerUsuarioId(getApplicationContext());
        String jwt = ApiClient.leerToken(getApplicationContext());

        if (jwt == null) {
            Log.e(TAG, "No hay JWT guardado. No se puede enviar token FCM.");
            return;
        }

        TokenRequest request = new TokenRequest(userId, tokenFCM);
        // Usar el JWT guardado para la autenticación
        Call<Void> call = ApiClient.getInmoServicio()
                .enviarToken("Bearer "+ jwt, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "🔥 Token actualizado correctamente en el servidor.");
                } else {
                    Log.e(TAG, "❌ Error al actualizar token: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "❌ Fallo de red al actualizar token", t);
            }
        });
    }


    /**
     * Crea y muestra una notificación en primer plano.
     * @param clickIntent Intent que se ejecuta al hacer click (opcional).
     */
    private void sendNotification(String title, String body, Intent clickIntent) {

        PendingIntent pendingIntent = null;
        if (clickIntent != null) {
            // Se usa el código de solicitud 0, pero se recomienda usar PendingIntent.FLAG_IMMUTABLE
            // en Android S y superior. Ya está incluido en el código.
            pendingIntent = PendingIntent.getActivity(this, 0, clickIntent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground) // Reemplaza con tu ícono
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        if (pendingIntent != null) {
            notificationBuilder.setContentIntent(pendingIntent);
        }

        // Para Android Oreo y superiores, se requiere un canal de notificación
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alertas de Ferretería",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Usamos un ID único (ej: el ID del producto) si está disponible, si no, usamos 0.
        int notificationId = (clickIntent != null && clickIntent.hasExtra("productId"))
                ? clickIntent.getIntExtra("productId", 0)
                : 0;

        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}