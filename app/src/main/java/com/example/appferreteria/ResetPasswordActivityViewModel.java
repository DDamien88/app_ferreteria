package com.example.appferreteria;

import android.app.Application;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.appferreteria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivityViewModel extends AndroidViewModel {

    private MutableLiveData<String> mensaje = new MutableLiveData<>();
    private static final String TAG = "ResetPasswordVM";

    public ResetPasswordActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getMensaje() {
        return mensaje;
    }

    public void enviarSolicitudReset(String email) {
        Log.d(TAG, "Solicitando reset para email: " + email);

        if (email.isEmpty()) {
            mensaje.setValue("Ingrese su correo electrónico");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mensaje.setValue("Ingrese un correo electrónico válido");
            return;
        }

        ApiClient.InmoServicio api = ApiClient.getInmoServicio();
        Call<String> call = api.resetPassword(email);

        Log.d(TAG, "Llamando a la API...");

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "Respuesta HTTP: " + response.code());
                Log.d(TAG, "Mensaje: " + response.body());

                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Éxito: " + response.body());
                    mensaje.setValue(response.body());
                } else {
                    Log.e(TAG, "Error: Código " + response.code());
                    mensaje.setValue("No se encontró el usuario con ese e-mail");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Fallo conexión: " + t.getMessage());
                mensaje.setValue("Error al conectar con el servidor");
            }
        });
    }
}