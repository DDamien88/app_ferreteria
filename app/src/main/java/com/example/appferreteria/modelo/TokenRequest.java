package com.example.appferreteria.modelo;

public class TokenRequest {

    private int usuarioId;
    private String deviceToken;

    public TokenRequest(int usuarioId, String deviceToken) {
        this.usuarioId = usuarioId;
        this.deviceToken = deviceToken;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
