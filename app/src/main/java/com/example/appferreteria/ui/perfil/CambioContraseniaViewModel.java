package com.example.appferreteria.ui.perfil;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appferreteria.R;
import com.example.appferreteria.modelo.CambiarPasswordRequest;
import com.example.appferreteria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambioContraseniaViewModel extends AndroidViewModel {
    private MutableLiveData<String> mensaje = new MutableLiveData<>();

    private MutableLiveData<Boolean> passwordCambiada = new MutableLiveData<>();


    public CambioContraseniaViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public LiveData<Boolean> getPasswordCambiada() {
        return passwordCambiada;
    }

    public void cambiarPassword(String currentPassword, String newPassword) {
        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
            mensaje.setValue("Complete ambos campos");
            return;
        }

        String token = ApiClient.leerToken(getApplication());
        ApiClient.InmoServicio api = ApiClient.getInmoServicio();


        CambiarPasswordRequest request = new CambiarPasswordRequest(currentPassword, newPassword);


        Call<Void> call = api.changePassword("Bearer " + token, request);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mensaje.setValue("Contraseña cambiada correctamente");

                } else {
                    mensaje.setValue("Error: la contraseña actual es incorrecta o hubo un problema");

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mensaje.setValue("Error de conexión con el servidor");

            }
        });
    }
}
