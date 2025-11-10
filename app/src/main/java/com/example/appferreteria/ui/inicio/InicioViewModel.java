package com.example.appferreteria.ui.inicio;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.appferreteria.modelo.Producto;
import com.example.appferreteria.request.ApiClient;
import java.util.List;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioViewModel extends AndroidViewModel {

    private MutableLiveData<List<Producto>> productosBajoStock = new MutableLiveData<>();

    public InicioViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Producto>> getProductosBajoStock() {
        return productosBajoStock;
    }

    // Método para cargar productos y filtrar por stock bajo
    public void cargarProductosBajoStock() {
        String token = ApiClient.leerToken(getApplication());
        ApiClient.InmoServicio api = ApiClient.getInmoServicio();

        Call<List<Producto>> call = api.obtenerProductos();
        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> todos = response.body();
                    // Filtrar productos con stock menor a 5 (por ejemplo)
                    List<Producto> bajoStock = todos.stream()
                            .filter(p -> p.getStock() < 5)
                            .collect(Collectors.toList());

                    productosBajoStock.postValue(bajoStock);
                } else {
                    Log.e("InicioViewModel", "Error al obtener productos: " + response.message());
                    productosBajoStock.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e("InicioViewModel", "Fallo al obtener productos", t);
                productosBajoStock.postValue(null);
            }
        });
    }
}
