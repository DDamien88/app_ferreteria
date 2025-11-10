package com.example.appferreteria.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Usuario implements Serializable {
    private int id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String password;
    private String rol;
    private boolean estado;
    private LocalDate fechaCreacion;
    private String tokenFireBase;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String apellido, String dni, String email, String telefono, String password, String rol, boolean estado, LocalDate fechaCreacion, String tokenFireBase) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.email = email;
        this.telefono = telefono;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.tokenFireBase = tokenFireBase;
    }

    public Usuario(String nombre, String apellido, String email, String password, String rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getTokenFireBase() {
        return tokenFireBase;
    }

    public void setTokenFireBase(String tokenFireBase) {
        this.tokenFireBase = tokenFireBase;
    }
}
