package com.example.appferreteria.ui.usuarios;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.appferreteria.modelo.Usuario;
import com.example.appferreteria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarUsuarioViewModel extends AndroidViewModel {

    public MutableLiveData<Boolean> registroExitoso = new MutableLiveData<>();
    public MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public RegistrarUsuarioViewModel(@NonNull Application application) {
        super(application);
    }

    public void registrarUsuario(String nombre, String apellido, String dni, String telefono, String email, String password, String rol) {
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() || rol.isEmpty()) {
            mensajeError.setValue("Todos los campos son obligatorios");
            return;
        }

        SharedPreferences sp = getApplication().getSharedPreferences("token.xml", Application.MODE_PRIVATE);
        String token = sp.getString("token", null);


        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setPassword(password);
        nuevoUsuario.setRol(rol);
        nuevoUsuario.setDni(dni);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setEstado(true);
        nuevoUsuario.setTokenFireBase("");

        Call<Void> call = ApiClient.getInmoServicio().registerUsuario("Bearer " + token, nuevoUsuario);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    registroExitoso.setValue(true);
                } else {
                    mensajeError.setValue("Error al registrar usuario. Código: " + response.code());
                    Log.d("error registro", response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mensajeError.setValue("Fallo de conexión: " + t.getMessage());
            }
        });
    }
}
