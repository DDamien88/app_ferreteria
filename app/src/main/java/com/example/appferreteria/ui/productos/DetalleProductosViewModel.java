package com.example.appferreteria.ui.productos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import com.example.appferreteria.R;
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
    private MutableLiveData<Boolean> navegar = new MutableLiveData<>();


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

    public LiveData<Boolean> getMNavegar(){
        return navegar;
    }


    public void darDeBajaProducto(Producto p) {
        p.setEstado(false);

        String token = ApiClient.leerToken(getApplication());

        // Convertimos el producto a JSON
        String productoJson = ApiClient.gson.toJson(p);
        RequestBody productoBody = RequestBody.create(MediaType.parse("multipart/form-data"), productoJson);

        // En este caso, no mandamos imagen nueva
        MultipartBody.Part imagenPart = null;


        Call<Void> call = ApiClient.getInmoServicio().eliminarProducto("Bearer " +
                token,
                p.getId()
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mensaje.postValue("Producto dado de baja correctamente");
                    navegar.postValue(true);
                } else {
                    mensaje.postValue("Error al dar de baja: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mensaje.postValue("Fallo en la conexión: " + t.getMessage());
            }
        });
    }
}
