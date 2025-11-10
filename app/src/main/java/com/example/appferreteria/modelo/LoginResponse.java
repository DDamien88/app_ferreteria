package com.example.appferreteria.modelo;

public class LoginResponse {
    private String token;
    private String rol;
    private String nombre;
    private String apellido;

    public String getToken() {
        return token;
    }

    public String getRol() {
        return rol;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }
}
