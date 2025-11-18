package com.example.appferreteria.modelo;

public class NotificacionRequest {

    private int usuarioId;
    private String titulo;
    private String mensaje;
    private String deviceToken; // opcional

    public NotificacionRequest(int usuarioId, String titulo, String mensaje, String deviceToken) {
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.deviceToken = deviceToken;
    }
}
