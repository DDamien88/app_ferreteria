package com.example.appferreteria.ui.perfil;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.appferreteria.modelo.Usuario;
import com.example.appferreteria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {

    private MutableLiveData<Usuario> usuario;
    private MutableLiveData<Boolean> mEstado = new MutableLiveData<>();
    private MutableLiveData<String> mNombre = new MutableLiveData<>();

    public PerfilViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Usuario> getUsuario() {
        if (usuario == null) {
            usuario = new MutableLiveData<>();
        }
        return usuario;
    }

    public LiveData<Boolean> getMEstado() {
        return mEstado;
    }

    public LiveData<String> getMNombre() {
        return mNombre;
    }


    public void cambioBoton(String nombreBoton, String nombre, String apellido, String dni, String email, String telefono, String rol ) {
        if (nombreBoton.equalsIgnoreCase("editar perfil")) {
            mEstado.setValue(true);
            mNombre.setValue("Guardar");
        }else {
            mEstado.setValue(false);
            mNombre.setValue("Editar");
            Usuario nuevo = new Usuario();
            nuevo.setId(usuario.getValue().getId());
            nuevo.setNombre(nombre);
            nuevo.setApellido(apellido);
            nuevo.setDni(dni);
            nuevo.setEmail(email);
            nuevo.setTelefono(telefono);
            nuevo.setRol(rol);
            String token = ApiClient.leerToken(getApplication());
            ApiClient.InmoServicio api = ApiClient.getInmoServicio();
            Call<Usuario> call = api.actualizarPerfil("Bearer " + token, nuevo);
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(getApplication(), "Datos guardados con éxito", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplication(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                        Log.d("error actualizar", response.message());
                    }
                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable throwable) {
                    Toast.makeText(getApplication(), "No se puede acceder a la API", Toast.LENGTH_SHORT).show();
                    //Log.e
                }
            });
        }
    }

    public void obtenerPerfil() {
        String token = ApiClient.leerToken(getApplication());

        ApiClient.InmoServicio api = ApiClient.getInmoServicio();
        Call<Usuario> call = api.obtenerPerfil("Bearer " + token);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    usuario.postValue(response.body());
                } else {
                    Toast.makeText(getApplication(), "perfil cargado correctamente", Toast.LENGTH_SHORT).show();
                    Log.d("PerfilVM", "Error en respuesta: " + response.code());

                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getApplication(), "Error de API", Toast.LENGTH_SHORT).show();
                Log.e("PerfilVM", "Error API: " + t.getMessage());

            }
        });
    }


}
