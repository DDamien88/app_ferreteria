package com.example.appferreteria.modelo;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * DTO que mapea la respuesta JSON del endpoint de C# a los campos de la tabla Notificacion.
 * Asumimos que el JSON devuelto por C# utiliza nombres de propiedades similares a PascalCase/camelCase,
 * pero los mapeamos explícitamente a los nombres de las columnas para seguridad.
 */
class NotificacionDTO {
    // Columna DB: idNotificacion (PK)
    @SerializedName("idNotificacion")
    public int id; // Usamos 'id' en Java por convención

    // Columna DB: usuario_id (FK)
    @SerializedName("usuarioId") // Asumo que C# lo serializa como 'usuarioId'
    public int usuario_id;

    // Columna DB: titulo
    @SerializedName("titulo")
    public String titulo;

    // Columna DB: mensaje
    @SerializedName("mensaje")
    public String mensaje;

    // Columna DB: leida
    @SerializedName("leida")
    public boolean leida;

    // Columna DB: fecha
    @SerializedName("fecha")
    public Date fecha;

    public interface NotificacionApi {
        String BASE_URL = "http://10.0.2.2:5000/"; // Ajusta a tu URL base

        /**
         * Obtiene la lista de notificaciones por usuario.
         * Endpoint: GET api/Notificaciones/{usuarioId}
         */
        @GET("api/Notificaciones/{usuarioId}")
        Call<List<NotificacionDTO>> getByUsuario(
                @Header("Authorization") String token,
                @Path("usuarioId") int usuarioId
        );

        /**
         * Marca una notificación específica como leída.
         * Endpoint: PUT api/Notificaciones/{id}/leida
         */
        @PUT("api/Notificaciones/{id}/leida")
        Call<Void> marcarLeida(
                @Header("Authorization") String token,
                @Path("id") int id // El 'id' aquí es el idNotificacion
        );
    }
}
