package com.example.appferreteria.ui.inicio;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appferreteria.modelo.MovimientoInventario;
import com.example.appferreteria.modelo.Producto;
import com.example.appferreteria.request.ApiClient;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioViewModel extends AndroidViewModel {

    private MutableLiveData<List<Producto>> productos = new MutableLiveData<>();
    private MutableLiveData<List<MovimientoInventario>> movimientos = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public InicioViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Producto>> getProductos() {
        return productos;
    }

    public LiveData<List<MovimientoInventario>> getMovimientos() {
        return movimientos;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void cargarResumen() {
        ApiClient.InmoServicio api = ApiClient.getInmoServicio();
        String token = ApiClient.leerToken(getApplication());


        Call<List<Producto>> callProd = api.obtenerProductos();
        Log.d("API_URL", callProd.request().url().toString());
        callProd.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productos.postValue(response.body());
                } else {
                    error.postValue("Error al cargar productos: " + response.code());
                    Log.d("InicioViewModel", "Error productos: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                error.postValue("Error de conexión: " + t.getMessage());
            }
        });


        Call<List<MovimientoInventario>> callMov = api.listarMovimientos("Bearer " + token);
        Log.d("API_URL", callMov.request().url().toString());
        callMov.enqueue(new Callback<List<MovimientoInventario>>() {
            @Override
            public void onResponse(Call<List<MovimientoInventario>> call, Response<List<MovimientoInventario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movimientos.postValue(response.body());
                } else {
                    error.postValue("Error al cargar movimientos: " + response.code());
                    Log.d("InicioViewModel", "Error movimientos: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<MovimientoInventario>> call, Throwable t) {
                error.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}
