package com.example.appferreteria.ui.productos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appferreteria.modelo.Producto;
import com.example.appferreteria.request.ApiClient;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleProductosViewModel extends AndroidViewModel {

    private MutableLiveData<Producto> producto = new MutableLiveData<>();
    private MutableLiveData<String> mensaje = new MutableLiveData<>();

    public DetalleProductosViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Producto> getProducto() {
        return producto;
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }

    public void setProducto(Producto p) {
        producto.setValue(p);
    }

    public void darDeBajaProducto(Producto p) {
        p.setEstado(false); // ⚠️ Dar de baja lógicamente

        String token = ApiClient.leerToken(getApplication());

        // Convertimos el producto a JSON
        String productoJson = ApiClient.gson.toJson(p);
        RequestBody productoBody = RequestBody.create(MediaType.parse("multipart/form-data"), productoJson);

        // En este caso, no mandamos imagen nueva
        MultipartBody.Part imagenPart = null;

        // Llamamos al endpoint PUT
        Call<Producto> call = ApiClient.getInmoServicio().actualizarProducto("Bearer " +
                token,
                p.getId(),
                imagenPart,
                productoBody
        );

        call.enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if (response.isSuccessful()) {
                    mensaje.postValue("Producto dado de baja correctamente");
                    producto.postValue(response.body());
                } else {
                    mensaje.postValue("Error al dar de baja: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                mensaje.postValue("Fallo en la conexión: " + t.getMessage());
            }
        });
    }
}
