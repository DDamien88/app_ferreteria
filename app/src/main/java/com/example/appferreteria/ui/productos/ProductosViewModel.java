package com.example.appferreteria.ui.productos;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appferreteria.modelo.Producto;
import com.example.appferreteria.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosViewModel extends AndroidViewModel {

    private MutableLiveData<List<Producto>> listaProductos = new MutableLiveData<>();

    private MutableLiveData<String> mMensaje = new MutableLiveData<>();
    private List<Producto> listadoCompleto = new ArrayList<>();

    public ProductosViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<List<Producto>> getListaProductos() {
        return listaProductos;
    }

    public LiveData<String> getMMensaje() {
        return mMensaje;
    }

    public void obtenerListaProductos() {
        String token = ApiClient.leerToken(getApplication());
        ApiClient.InmoServicio api = ApiClient.getInmoServicio();
        Call<List<Producto>> call = api.obtenerProductos();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful()) {
                    listadoCompleto = response.body();
                    listaProductos.postValue(listadoCompleto);
                } else {
                    Toast.makeText(getApplication(), "No se obtuvieron productos", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable throwable) {
                Log.d("errorProducto", throwable.getMessage());

                Toast.makeText(getApplication(), "Error al obtener productos", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void filtrarProductos(String query) {
        if (query == null || query.isEmpty()) {
            listaProductos.setValue(listadoCompleto);
            mMensaje.setValue("");
            return;
        }

        List<Producto> filtrados = new ArrayList<>();
        for (Producto p : listadoCompleto) {
            if (p.getNombre().toLowerCase().contains(query.toLowerCase()) ||
                    p.getCategoria().toLowerCase().contains(query.toLowerCase())) {
                filtrados.add(p);
            }
        }

        if (filtrados.isEmpty()) {
            mMensaje.setValue("No se encontraron resultados con esa búsqueda");
        } else {
            mMensaje.setValue("");
        }

        listaProductos.setValue(filtrados);
    }

}
