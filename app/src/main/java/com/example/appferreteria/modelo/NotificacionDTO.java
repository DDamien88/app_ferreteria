package com.example.appferreteria.modelo;

import com.google.gson.annotations.SerializedName;

/**
 * DTO que mapea la respuesta JSON del endpoint de C# a los campos de la tabla Notificacion.
 * Asumimos que el JSON devuelto por C# utiliza nombres de propiedades similares a PascalCase/camelCase,
 * pero los mapeamos explícitamente a los nombres de las columnas para seguridad.
 */
class NotificacionDTO {

    @SerializedName("id")
    public int id;

    @SerializedName("usuarioId")
    public int usuarioId;

    @SerializedName("titulo")
    public String titulo;

    @SerializedName("mensaje")
    public String mensaje;

    @SerializedName("leida")
    public boolean leida;

    @SerializedName("fecha")
    public String fecha; // mejor String, no Date

    @SerializedName("usuarioNombre")
    public String usuarioNombre;

    @SerializedName("usuarioApellido")
    public String usuarioApellido;
}

