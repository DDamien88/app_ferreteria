package com.example.appferreteria.ui.notificaciones;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appferreteria.modelo.Notificacion;
import com.example.appferreteria.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificacionViewModel extends AndroidViewModel {

    private MutableLiveData<List<Notificacion>> notificacionesMutable;

    public NotificacionViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Notificacion>> getNotificaciones() {
        if (notificacionesMutable == null) {
            notificacionesMutable = new MutableLiveData<>();
        }
        return notificacionesMutable;
    }

    public void cargarNotificaciones() {
        String token = ApiClient.leerToken(getApplication());
        //int userId = ApiClient.obtenerUsuarioId(getApplication());

        Call<List<Notificacion>> call = ApiClient.getInmoServicio()
                .obtenerNotificaciones("Bearer " + token);

        call.enqueue(new Callback<List<Notificacion>>() {
            @Override
            public void onResponse(Call<List<Notificacion>> call, Response<List<Notificacion>> response) {
                if (response.isSuccessful()) {
                    List<Notificacion> lista = response.body();
                    notificacionesMutable.postValue(lista);
                } else {
                    Log.e("NOTIF_VM", "Error HTTP: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Notificacion>> call, Throwable t) {
                Log.e("NOTIF_VM", "Fallo de red", t);
            }
        });
    }
}
