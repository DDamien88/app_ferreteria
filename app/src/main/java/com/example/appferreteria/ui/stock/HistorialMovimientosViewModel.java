package com.example.appferreteria.ui.stock;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appferreteria.modelo.MovimientoInventario;
import com.example.appferreteria.request.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialMovimientosViewModel extends AndroidViewModel {

    private final MutableLiveData<List<MovimientoInventario>> movimientos = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    public HistorialMovimientosViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<MovimientoInventario>> getMovimientos() {
        return movimientos;
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    // Cargar todos los movimientos
    public void cargarTodos() {
        String token = ApiClient.leerToken(getApplication());
        ApiClient.InmoServicio api = ApiClient.getInmoServicio();

        Call<List<MovimientoInventario>> call = api.listarMovimientos("Bearer " + token);

        call.enqueue(new Callback<List<MovimientoInventario>>() {
            @Override
            public void onResponse(Call<List<MovimientoInventario>> call, Response<List<MovimientoInventario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movimientos.setValue(response.body());
                } else {
                    mensaje.setValue("Error al cargar movimientos");
                }
            }

            @Override
            public void onFailure(Call<List<MovimientoInventario>> call, Throwable t) {
                Log.d("fallo listar", t.getMessage());
                mensaje.setValue("Fallo al conectarse: " + t.getMessage());
            }
        });
    }

    // Cargar movimientos por código de barras
    public void cargarPorCodigo(String codigo) {
        String token = ApiClient.leerToken(getApplication());
        ApiClient.InmoServicio api = ApiClient.getInmoServicio();

        Call<List<MovimientoInventario>> call = api.listarMovimientosPorProducto("Bearer " + token, codigo);

        call.enqueue(new Callback<List<MovimientoInventario>>() {
            @Override
            public void onResponse(Call<List<MovimientoInventario>> call, Response<List<MovimientoInventario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    movimientos.setValue(response.body());
                } else {
                    mensaje.setValue("No se encontraron movimientos para el código: " + codigo);
                    movimientos.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<MovimientoInventario>> call, Throwable t) {
                mensaje.setValue("Fallo al conectarse por codigo: " + t.getMessage());
            }
        });
    }
}
