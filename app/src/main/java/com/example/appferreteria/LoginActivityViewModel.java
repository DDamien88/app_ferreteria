package com.example.appferreteria;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.appferreteria.modelo.LoginResponse;
import com.example.appferreteria.modelo.Usuario;
import com.example.appferreteria.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivityViewModel extends AndroidViewModel {

    //llamada tel
    private SensorManager manager;
    private Sensor acelerometro;
    private ManejaEventos manejador;
    private MutableLiveData<String> mShake;

    private MutableLiveData<String> mMensaje;

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
    }

    private static final float SHAKE_THRESHOLD = 12.0f;
    private long lastShakeTime = 0;


    public LiveData<String> getShake() {
        if (mShake == null) {
            mShake = new MutableLiveData<>();
        }
        return mShake;
    }

    public void activarSensor() {
        manager = (SensorManager) getApplication().getSystemService(Context.SENSOR_SERVICE);
        acelerometro = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (acelerometro != null) {
            manejador = new ManejaEventos();
            manager.registerListener(manejador, acelerometro, SensorManager.SENSOR_DELAY_UI);

        }
    }

    public void desactivarSensor() {
        if (manager != null && manejador != null) {
            manager.unregisterListener(manejador);
        }
    }

    private class ManejaEventos implements SensorEventListener {

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double acceleration = Math.sqrt(x * x + y * y + z * z)
                        - SensorManager.GRAVITY_EARTH;

                long currentTime = System.currentTimeMillis();

                if (acceleration > SHAKE_THRESHOLD && (currentTime - lastShakeTime) > 2000) {
                    lastShakeTime = currentTime;
                    mShake.setValue("SHAKE");
                }
                Log.d("SHAKE_TEST", "x=" + x + " y=" + y + " z=" + z + " accel=" + acceleration);

            }
        }
    }

    public void hacerLlamada(Context context) {
        String numero = "2664505954";
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numero));

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "No tiene permiso para llamar", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(context, "Llamando a la ferretería...", Toast.LENGTH_SHORT).show();
        context.startActivity(intent);
    }


    public LiveData<String> getMMensaje() {
        if (mMensaje == null) {
            mMensaje = new MutableLiveData<>();
        }
        return mMensaje;
    }

    public void login(String usuario, String contrasenia) {

        ApiClient.InmoServicio inmoServicio = ApiClient.getInmoServicio();
        Call<LoginResponse> call = inmoServicio.login(usuario, contrasenia);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    String token = loginResponse.getToken();
                    String rol = loginResponse.getRol();

                    Usuario user = new Usuario();
                    user.setRol(rol);
                    user.setNombre(loginResponse.getNombre());
                    user.setApellido(loginResponse.getApellido());

                    // Guardamos token + rol del usuario
                    ApiClient.guardarToken(getApplication(), token, user);

                    Toast.makeText(getApplication(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    Log.d("TOKEN", token);
                    Log.d("ROL", rol);

                    // Navegamos a la actividad principal
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getApplication().startActivity(intent);

                } else {
                    Log.e("LOGIN_ERROR", "Código: " + response.code() + " | " + response.message());
                    Toast.makeText(getApplication(), "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LOGIN_FAIL", t.getMessage());
                Toast.makeText(getApplication(), "Error al conectar con la API", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
